package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuber_mobile_application.Controllers.Client_ModuleController;
import com.tuber_mobile_application.Controllers.ModuleController;
import com.tuber_mobile_application.Controllers.Tutor_ModuleController;

import com.tuber_mobile_application.Models.Client_Module;
import com.tuber_mobile_application.Models.Module;
import com.tuber_mobile_application.Models.Tutor_Module;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.HelperMethods;
import com.tuber_mobile_application.helper.SessionManagement;
import com.tuber_mobile_application.helper.ui.ModuleAdapter;
import com.tuber_mobile_application.helper.ui.MyModuleItem;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OfferedModulesFragment extends Fragment
{
    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MyModuleItem> moduleList;
    private ModuleController moduleController;
    private Tutor_ModuleController tutor_moduleController;
    private Client_ModuleController client_moduleController;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_offered_modules,container,false);
        setUpRetrofit();
        getActivity().setTitle("Modules Offered");
        moduleList = new ArrayList<>();

        getModules();

        recyclerView = view.findViewById(R.id.offered_modules_recyclerView);
        return view;
    }

    /**
     *
     */
    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        moduleController = retrofit.get().create(ModuleController.class);
        client_moduleController = retrofit.get().create(Client_ModuleController.class);
        tutor_moduleController = retrofit.get().create(Tutor_ModuleController.class);
    }

    /**
     *
     */
    private void getModules() {
        SessionManagement sessionManagement = new SessionManagement(getContext());
        final int userID = sessionManagement.getSession();
        Call<List<Module>> call = moduleController.getAllModules();;

        if(call != null){
            call.enqueue(new Callback<List<Module>>() {
                @Override
                public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                    if (!response.isSuccessful()) {
                        //todo: display error message
                        return;
                    }
                    //get the modules
                    List<Module> modules = response.body();
                    moduleList = new ArrayList<>();

                    assert modules != null;
                    for (Module module : modules) {
                        moduleList.add(new MyModuleItem(module.getId(),module.getModule_Name(), module.getModule_Code(), R.drawable.ic_add));
                    }

                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    adapter = new ModuleAdapter(moduleList);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new ModuleAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int pos) {
                            //Todo: Display Selected Module Information
                            Toast.makeText(getContext(), "You Clicked Item #: " + pos, Toast.LENGTH_SHORT).show();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onActionClick(final int pos)
                        {
                            final DialogInterface.OnClickListener startSessionListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                        {
                                            //Get the current date
                                            @SuppressLint("SimpleDateFormat")
                                            java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                            dateFormat.format(Calendar.getInstance().getTime());
                                            //String now  = HelperMethods.getCurrentDateTime();
                                            String now  =  dateFormat.format(Calendar.getInstance().getTime());

                                            SessionManagement sessionManagement = new SessionManagement(getContext());

                                            //Check the type of account currently in use
                                            if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                                            {
                                                final int clientID = sessionManagement.getClientSession();
                                                Client_Module client_module = new Client_Module(0,now,1,null,null,moduleList.get(pos).getModuleID(),clientID);
                                                addClientModule(client_module);
                                            }
                                            else
                                            {
                                                final int tutorID = sessionManagement.getTutorSession();
                                                Tutor_Module tutor_module = new Tutor_Module(0,now,1,tutorID,moduleList.get(pos).getModuleID() ,null,null);
                                                addTutorModule(tutor_module);
                                            }
                                            Toast.makeText(getContext(), "Module Added To Your List", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // do nothing for now
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            builder.setMessage("Do you want to add " + moduleList.get(pos).getModuleName() +" to your modules?") // alert box question
                                    .setPositiveButton("Yes", startSessionListener) // yes button
                                    .setNegativeButton("No", startSessionListener).show(); // no button



                        }
                    });
                }
                @Override
                public void onFailure(Call<List<Module>> call, Throwable t) {
                    Toast.makeText(getContext(), "Could not load Modules", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     *
     * @param clientModule -
     */

    private void addClientModule(Client_Module clientModule){

        Call<String> call = client_moduleController.AddNewClientModuleBridge_CollisionChecking(clientModule.getClientRef(),App_Global_Variables.USER_CURRENT_STATUS,clientModule);

        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Bad Api response", Toast.LENGTH_SHORT).show();
                    return;
                }
                //response
                String _response = response.body();
                //move to myModule fragment
                //Fragment selectedFragment  = new MyModulesFragment();
                //getFragmentManager().beginTransaction().replace(R.id.module_fragment_container, selectedFragment).commit();

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Could not Added Module", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param tutor_module
     */
    private void addTutorModule(Tutor_Module tutor_module){
        Call<String> call = tutor_moduleController.AddNewTutorModuleBridge_CollisionChecking(tutor_module.getTutor_Reference(),App_Global_Variables.USER_CURRENT_STATUS,tutor_module);

        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Bad Api response", Toast.LENGTH_SHORT).show();
                    return;
                }
                //response
                String _response = response.body();
                //move to myModule fragment
                //Fragment selectedFragment  = new MyModulesFragment();
                //getFragmentManager().beginTransaction().replace(R.id.module_fragment_container, selectedFragment).commit();

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Could not Added Module", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @return - current date time
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return  new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }

}
