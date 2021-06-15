package com.tuber_mobile_application;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.Client_ModuleController;
import com.tuber_mobile_application.Controllers.ModuleController;
import com.tuber_mobile_application.Controllers.Tutor_ModuleController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.Client;
import com.tuber_mobile_application.Models.Client_Module;
import com.tuber_mobile_application.Models.Module;
import com.tuber_mobile_application.Models.User;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.SessionManagement;
import com.tuber_mobile_application.helper.ui.ModuleAdapter;
import com.tuber_mobile_application.helper.ui.MyModuleItem;

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

public class MyModulesFragment extends Fragment
{
    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MyModuleItem> moduleList;
    private ModuleController moduleController;
    private Client_ModuleController client_moduleController;
    private Tutor_ModuleController tutor__moduleController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_modules,container,false);
        setUpRetrofit();
        getActivity().setTitle("My Modules");

        getModules();
        recyclerView = view.findViewById(R.id.my_modules_recycler_view);

        return view;
    }

    /**
     * set upt the retrofit connection
     */
    private void setUpRetrofit() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        moduleController = retrofit.get().create(ModuleController.class);
        client_moduleController = retrofit.get().create(Client_ModuleController.class);
        tutor__moduleController = retrofit.get().create(Tutor_ModuleController.class);
    }

    private void getModules() {

        SessionManagement sessionManagement = new SessionManagement(getContext());
        final int userID = sessionManagement.getSession();
        Call<List<Module>> call = null;

        if(App_Global_Variables.USER_CURRENT_STATUS.toString().equals(App_Global_Variables.CLIENT_STATUS.toString()))
            call = moduleController.GetClientModulesByUserTableID_Mobile(userID);
        else
            call = moduleController.GetTutorModulesByUserTableID(userID);

        if(call != null){
            call.enqueue(new Callback<List<Module>>() {
                @Override
                public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {

                    if (!response.isSuccessful()) {
                        //todo: display error message
                        return;
                    }
                    //get the modules
                    final List<Module> modules = response.body();
                    moduleList = new ArrayList<>();
                    for(Module module : modules){
                        moduleList.add(new MyModuleItem(module.getId(),module.getModule_Name(),module.getModule_Code(),R.drawable.ic_delete));
                    }

                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    adapter = new ModuleAdapter(moduleList);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new ModuleAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int pos) {
                            //Todo: Display Remove Button
                            Toast.makeText(getContext(), "Module Name: "+ moduleList.get(pos).getModuleName(), Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onActionClick(final int pos)
                        {
                            final DialogInterface.OnClickListener startSessionListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                        {
                                            if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                                                removeClientModule(pos);
                                            else
                                                removeTutorModule(pos);
                                        }
                                        break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // do nothing for now
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            builder.setMessage("You do want to remove "+ modules.get(pos).getModule_Name() +" from your module list?") // alert box question
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
     * remove the tutor module
     * @param pos
     */
    private void removeTutorModule(int pos) {
        final int pos_ = pos;
        MyModuleItem module = moduleList.get(pos);
        SessionManagement sessionManagement = new SessionManagement(this.getContext());
        final int tutorID = sessionManagement.getTutorSession();
        if(tutorID > 0){
            Call<Integer> call = tutor__moduleController.removeModule(tutorID + "_" + module.getModuleID());
            call.enqueue(new Callback<Integer>(){
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(),"Something went wrong, please retry" , Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int Id = response.body();
                    moduleList.remove(pos_);
                    adapter.notifyItemRemoved(pos_);
                    Toast.makeText(getContext(),moduleList.get(pos_).getModuleName()+ "has been Removed" , Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(getContext(),"Something wen wrong, Please check you internet connection" , Toast.LENGTH_SHORT).show();
                }
            });
        }else
        {

        }


    }

    /**
     * remove the client module
     * @param pos
     */
    private void removeClientModule(int pos) {

        final int pos_ = pos;
        MyModuleItem module = moduleList.get(pos);
        SessionManagement sessionManagement = new SessionManagement(this.getContext());
        final int clientID = sessionManagement.getClientSession();
        if(clientID > 0){
            String parameter = clientID + "_" + module.getModuleID();
            Call<Integer> call = client_moduleController.removeModule(parameter);
            call.enqueue(new Callback<Integer>(){
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(),"Something went wrong, please retry" , Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getContext(),moduleList.get(pos_).getModuleName()+ "has been Removed" , Toast.LENGTH_SHORT).show();
                    moduleList.remove(pos_);
                    adapter.notifyItemRemoved(pos_);

                }
                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(getContext(),"Something wen wrong, Please check you internet connection" , Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     *  remove a module as specified by th user
     * @param pos
     */
    public void removeItem(int pos){

    }

    public void makeRecyclerView(){
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new ModuleAdapter(moduleList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new ModuleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                //Todo: Display Remove Button
                Toast.makeText(getContext(), "Module Name: "+ moduleList.get(pos).getModuleName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(int pos) {
                removeItem(pos);
            }
        });
    }

}
