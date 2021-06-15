package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.InvoiceController;
import com.tuber_mobile_application.helper.App_Global_Variables;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FromPayPalFragment extends Fragment
{
    private View fromPayPalView;
    private InvoiceController invoiceController; // used to change invoice status

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        fromPayPalView = inflater.inflate(R.layout.fragment_from_paypal, container, false);
        Objects.requireNonNull(getActivity()).setTitle("Post Payment Information");

        Bundle bundle = this.getArguments(); // getting argument from payment fragment
        if(bundle != null)
        {
            try
            {
                JSONObject paymentObj = new JSONObject(bundle.getString("PaymentDetails"));
                displayConfirmation(paymentObj.getJSONObject("response"), bundle.getString("PaymentAmount"), bundle.getString("Invoices"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        goToDashboard();

        return fromPayPalView;
    }

    @SuppressLint("SetTextI18n")
    private void displayConfirmation(JSONObject response, String paymentAmount,String invoices)
    {
        handlePayments(invoices);
        TextView txtDetails = fromPayPalView.findViewById(R.id.details);

        try
        {
            String paymentID = response.getString("id"); // invoice id
            String paymentStatus = response.getString("state"); // approved || canceled
            String amount = "R "+ paymentAmount; // paid amount

            txtDetails.setText("Invoice ID: " + paymentID +
                    "\n Payment Status: " + paymentStatus +
                    "\n Amount Paid: " + amount);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void handlePayments(String invoices)
    {
        setUpTuberRetrofit();
        StringTokenizer invoiceTokens = new StringTokenizer(invoices,"/");
        while (invoiceTokens.hasMoreTokens())
        {
            final int id = Integer.parseInt(invoiceTokens.nextToken());
            Call<Integer> call = invoiceController.changePaymentStatus(id);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
                {
                    if(response.body() != null)
                    {
                        if(id == response.body()){
                            Toast.makeText(getContext(), id + " Payment Cleared", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                }
            });

        }

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
    }


    private void goToDashboard()
    {
        Button btnGo = fromPayPalView.findViewById(R.id.btnGoHome);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                assert getFragmentManager() != null;
                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
                {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new TutorDashboardFragment()).commit();
                }
                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                 }
            }
        });
    }
}
