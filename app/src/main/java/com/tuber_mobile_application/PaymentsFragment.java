package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Controllers.GenericController;
import com.tuber_mobile_application.Controllers.InvoiceController;
import com.tuber_mobile_application.Models.Client;
import com.tuber_mobile_application.Models.Currencies;
import com.tuber_mobile_application.Models.Invoice;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.SessionManagement;
import com.tuber_mobile_application.helper.ui.InvoiceAdapter;
import com.tuber_mobile_application.helper.ui.InvoiceItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;

public class PaymentsFragment extends Fragment
{
    // sand box test acc
    // email: client@tuber.com password: testData123

    public static final int PAY_PAL_REQUEST_CODE = 7171; // request code for payment intent
    private View paymentView; // for global access
    private double amount; // amount to be paid

    private GenericController currencyController; // to get the latest rand => dollar value
    private InvoiceController invoiceController; // to get outstanding invoices from the db
    private ClientController clientController; // used to get user client id

    private RecyclerView recyclerView;
    private InvoiceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<InvoiceItem> invoiceItems;

    private ProgressBar amountProgressBar, cardProgressBar; // loaders
    private TextView txtTotalAmountDue;
    private Button btnPay;

    public String invoiceIDs = ""; // holds all of the unpaid invoices, format: id1/id2/id3

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // for the testing area
            .clientId(App_Global_Variables.PAYPAL_CLIENT_ID); // passing in our client id, so that pay_pal knows what acc to insert the money in


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        paymentView = inflater.inflate(R.layout.fragment_payments,container,false);

        setUpRetrofit();
        setUpTuberRetrofit();
        viewControlsInitialization(); // initialization of controls

        //start Payment Service
        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        Objects.requireNonNull(getActivity()).startService(intent);

        processPayment();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });

        return paymentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();


    }

    private void processPayment()
    {
        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
        {
            SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
            final int userID = sessionManagement.getSession();

            Call<Client> integerCall = clientController.getClient(userID);
            integerCall.enqueue(new Callback<Client>() {
                @Override
                public void onResponse(@NotNull Call<Client> call, @NotNull Response<Client> response)
                {
                    if(response.isSuccessful())
                    {
                        if(response.body() != null)
                        {
                            Call<List<Invoice>> invoiceCall = invoiceController.getUnpaidInvoices(response.body().getId());
                            invoiceCall.enqueue(new Callback<List<Invoice>>() {
                                @Override
                                public void onResponse(@NotNull Call<List<Invoice>> call, @NotNull Response<List<Invoice>> response) {
                                    if (response.isSuccessful()) {
                                        List<Invoice> invoices = response.body();
                                        if (invoices != null) {

                                            if (invoices.isEmpty()) {
                                                TextView txtNoInvoice = paymentView.findViewById(R.id.no_invoice);
                                                txtNoInvoice.setVisibility(View.VISIBLE); // inform user that they don't have invoices
                                                makeLoadersInvisible(); // hiding loaders
                                                btnPay.setVisibility(View.INVISIBLE); // hiding pay button
                                                return;
                                            }

                                            invoiceItems = new ArrayList<>();

                                            for (Invoice invoice : invoices)
                                            {
                                                invoiceIDs += invoice.getId() + "/";
                                                // adding items to the view
                                                invoiceItems.add(new InvoiceItem(invoice.getAmount(), invoice.getDescription(), invoice.getDate_Issued()));
                                                amount += Double.parseDouble(invoice.getAmount()); // accumulating all of the payments for a total
                                            }

                                            txtTotalAmountDue.setText(String.valueOf(BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_EVEN))); // rounding off the double to 2 decimal places

                                            recyclerView.setHasFixedSize(true);
                                            layoutManager = new LinearLayoutManager(getContext());
                                            adapter = new InvoiceAdapter(invoiceItems);
                                            recyclerView.setLayoutManager(layoutManager);
                                            recyclerView.setAdapter(adapter);

                                            makeLoadersInvisible(); // hiding loaders

                                        }
                                    } else {
                                        makeLoadersInvisible(); // hiding loaders
                                        Toast.makeText(getContext(), "Couldn't load your invoices", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(@NotNull Call<List<Invoice>> call, @NotNull Throwable t) {
                                    Toast.makeText(getContext(), "Couldn't load your invoices", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }

                }

                @Override
                public void onFailure(@NotNull Call<Client> call, @NotNull Throwable t) {

                }
            });

        }
        else
        {
            TextView txtNoInvoice = paymentView.findViewById(R.id.no_invoice);
            txtNoInvoice.setVisibility(View.VISIBLE); // inform user that they don't have invoices
            makeLoadersInvisible(); // hiding loaders
            btnPay.setVisibility(View.INVISIBLE); // hiding pay button
        }

    }

    private void makePayment()
    {
        Call<Currencies> currenciesCall = currencyController.getCurrencies();
        currenciesCall.enqueue(new Callback<Currencies>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NotNull Call<Currencies> call, @NotNull Response<Currencies> response)
            {
                if(!response.isSuccessful()){ return; }

                if(response.body() != null)
                {
                    double dollars = amount / response.body().getRates().getZAR(); // converting from rands => dollars

                    PayPalPayment payPalPayment = new PayPalPayment(BigDecimal.valueOf(dollars).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                            "USD","Tuber October Tutorial Sessions", PayPalPayment.PAYMENT_INTENT_SALE); // todo: make the name & value dynamic but from api

                    Intent intent = new Intent(getContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent,PAY_PAL_REQUEST_CODE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Currencies> call, @NotNull Throwable t)
            {
                Toast.makeText(getContext(), "Couldn't convert your amount to dollars", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeLoadersInvisible()
    {
        cardProgressBar.setVisibility(View.GONE);
        amountProgressBar.setVisibility(View.GONE);
    }

    private void viewControlsInitialization()
    {
        recyclerView = paymentView.findViewById(R.id.invoice_recycler_view);
        amountProgressBar = paymentView.findViewById(R.id.pay_amount_loader);
        cardProgressBar = paymentView.findViewById(R.id.pay_information_loader);
        txtTotalAmountDue = paymentView.findViewById(R.id.pay_tot_amount);
        btnPay = paymentView.findViewById(R.id.btn_pay);
    }

    private void setUpTuberRetrofit()
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL) // for our api calls
                .addConverterFactory(GsonConverterFactory.create(gson))// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        invoiceController = retrofit.get().create(InvoiceController.class);
        clientController = retrofit.get().create(ClientController.class);
    }

    private void setUpRetrofit() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl("https://api.ratesapi.io") // api for base currency base conversions
                .addConverterFactory(GsonConverterFactory.create(gson))// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        currencyController = retrofit.get().create(GenericController.class);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode == PAY_PAL_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                assert data != null;
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null)
                {
                    try
                    {
                        String details = confirmation.toJSONObject().toString(4);

                        FromPayPalFragment fromPayPalFragment = new FromPayPalFragment(); // fragment we going to

                        Bundle bundle = new Bundle();
                        bundle.putString("PaymentDetails", details);
                        bundle.putString("PaymentAmount", "" + amount);
                        bundle.putString("Invoices", "" + invoiceIDs);

                        fromPayPalFragment.setArguments(bundle); // setting fragment arguments
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fromPayPalFragment).commit();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            {
                Toast.makeText(getContext(), "Invalid", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == PaymentActivity.RESULT_CANCELED)
            {
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        // stop pay pal service when fragment is closed
        Objects.requireNonNull(getActivity()).stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }

}
