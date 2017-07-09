package com.mumineendownloads.mumineenpdf.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Adapters.RequestAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.ChatDivider;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDFReq;
import com.mumineendownloads.mumineenpdf.Model.User;
import com.mumineendownloads.mumineenpdf.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class RequestPage extends Fragment {
    RecyclerView mRecyclerView;
    private RequestAdapter mRequestAdapter;
    ArrayList<PDFReq.Request> mRequests;
    private Toolbar mActivityActionBarToolbar;

    public RequestPage newInstance() {
        return new RequestPage();
    }

    public RequestPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_request_page, container, false);
        final User user = Utils.getUser(getContext());
        mRecyclerView = (RecyclerView) v.findViewById(R.id.messagesContainer);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mActivityActionBarToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        mActivityActionBarToolbar.setTitle("Mumineen Downloads - Request PDF");
        Fonty.setFonts(mActivityActionBarToolbar);
        ImageButton imageButton = (ImageButton) v.findViewById(R.id.chatSendButton);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        final EditText editText = (EditText) v.findViewById(R.id.messageEdit);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequest();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()!=0) {
                    submitRequest(editText.getText().toString());
                    PDFReq.Request request = new PDFReq.Request();
                    request.setUser_id(String.valueOf(user.getUserId()));
                    request.setRequest(editText.getText().toString());
                    request.setId(mRequests.get(mRequests.size()-1).getId()+1);
                    mRequests.add(0,request);
                    mRecyclerView.scrollToPosition(0);
                    mRequestAdapter.notifyItemInserted(0);
                    editText.setText("");
                }
            }
        });
        getRequest();
        return v;
    }

    private void submitRequest(String message) {
        if(Utils.isLogged(getContext())) {
            sendRequest(message);
        }else {
            showRegister(message);
        }
    }

    private void showRegister(final String message){
        final MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .typeface("myfonts.ttf","myfonts.ttf")
                        .title("Please register to continue")
                        .customView(R.layout.register_form, true)
                        .positiveText("Register")
                        .neutralText("Login")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                showLoginDialog(message);
                            }
                        })
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .build();
        dialog.show();
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(dialog,message);
            }
        });
    }

    private void showLoginDialog(final String message) {
        new MaterialDialog.Builder(getActivity())
                .title("Login")
                .customView(R.layout.register_form,true)
                .positiveText("Login")
                .negativeText("Cancel")
                .neutralText("Register")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showRegister(message);
                    }
                })
                .build()
                .show();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        Log.e(email, String.valueOf(m.matches()));
        return m.matches();
    }

    private void register(final MaterialDialog dialog, final String message) {
        View v = dialog.getCustomView();
        final MaterialEditText name = (MaterialEditText) v.findViewById(R.id.name);
        final MaterialEditText email = (MaterialEditText) v.findViewById(R.id.email);
        MaterialEditText pass = (MaterialEditText) v.findViewById(R.id.pass);


        if(name.getText().length()==0
                || email.getText().length()==0
                || !isValidEmailAddress(email.getText().toString())
                || pass.getText().length()==0) {
            if (name.getText().length() == 0) {
                name.setError("Please enter a valid name");
            } else {
                name.setError(null);
            }


            if (email.getText().length() == 0 || !isValidEmailAddress(email.getText().toString())) {
                email.setError("Please enter a valid email");
            } else {
                email.setError(null);
            }

            if (pass.getText().length() == 0) {
                pass.setError("Please enter a valid pass");
            } else {
                pass.setError(null);
            }
        } else {
            email.setError(null);
            pass.setError(null);
            name.setError(null);
            final RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://mumineendownloads.com/app/register_user.php";
            final String nameString = name.getText().toString();
            final String emailString = email.getText().toString();
            final String passwordString = pass.getText().toString();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            switch (response) {
                                case "exists":
                                    email.setError("Email already registered");
                                    break;
                                case "-1":
                                    break;
                                default:
                                    dialog.dismiss();
                                    try {
                                        int id = Integer.parseInt(response);
                                        registerLocally(id, email.getText().toString(), name.getText().toString(),message);
                                    }catch (NumberFormatException ignored){

                                    }
                                    break;
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }

            }) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("name", nameString);
                    params.put("email", emailString);
                    params.put("password", passwordString);
                    return params;
                }
            };
            queue.add(stringRequest);
        }


    }

    private void registerLocally(int id, String email, String name, String message) {
        Utils.registerUser(name,email,id,getContext());
        sendRequest(message);
    }

    private void sendRequest(final String message) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://mumineendownloads.com/app/sendRequest.php";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     Log.e("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams()
            {
                User user = Utils.getUser(getContext());
                Map<String, String>  params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(user.getUserId()));
                params.put("user_name", user.getName());
                params.put("message", message);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void getRequest(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://www.mumineendownloads.com/app/getRequests.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      parseData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    private void parseData(String response) {
        Gson gson = new Gson();
        mRequests = gson.fromJson(response, new TypeToken<ArrayList<PDFReq.Request>>() {
        }.getType());
        mRequestAdapter = new RequestAdapter(mRequests,getContext());
        mRecyclerView.addItemDecoration(new ChatDivider(getContext()));
        mRecyclerView.setAdapter(mRequestAdapter);
    }

    public boolean getRegistered() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return pref.getBoolean("registered",false);
    }
}
