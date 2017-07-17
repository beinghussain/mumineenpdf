package com.mumineendownloads.mumineenpdf.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

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
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Model.PDFReq;
import com.mumineendownloads.mumineenpdf.Model.SelectFile;
import com.mumineendownloads.mumineenpdf.Model.User;
import com.mumineendownloads.mumineenpdf.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.mumineendownloads.mumineenpdf.Fragments.SelectFileFragment.RESULT_FILE;


public class RequestPage extends Fragment {
    public RecyclerView mRecyclerView;
    private RequestAdapter mRequestAdapter;
    ArrayList<PDFReq.Request> mRequests;
    private Toolbar mActivityActionBarToolbar;
    private EditText editText;
    private User user;
    private boolean isUpload;
    private ImageButton imageButton;
    private RelativeLayout loading;

    public RequestPage newInstance() {
        return new RequestPage();
    }

    public RequestPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)     {
        final View v = inflater.inflate(R.layout.fragment_request_page, container, false);
         user = Utils.getUser(getContext());
        mRecyclerView = (RecyclerView) v.findViewById(R.id.messagesContainer);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mActivityActionBarToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        mActivityActionBarToolbar.setTitle("Request PDF");
        Fonty.setFonts(mActivityActionBarToolbar);
        loading = (RelativeLayout) v.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        imageButton = (ImageButton) v.findViewById(R.id.chatSendButton);
        editText = (EditText) v.findViewById(R.id.messageEdit);
        imageButton.setImageResource(R.drawable.ic_file_upload_black_24dp);
        imageButton.setImageResource(R.drawable.ic_file_upload_black_24dp);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.normal(getContext(),"Uploading").show();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                   isUpload = true;
                    changeButton();
                }else {
                    isUpload=false;
                    changeButton();
                }
            }
        });

        getRequest();
        return v;
    }

    private void changeButton() {
        if(isUpload){
           imageButton.setImageResource(R.drawable.ic_file_upload_black_24dp);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Toasty.normal(getContext(),"Uploading").show();
                }
            });
        }else {
            imageButton.setImageResource(R.drawable.ic_send_black_24dp);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editText.getText().toString().length()!=0) {
                        submitRequest(editText.getText().toString(),editText,user);
                    }
                }
            });
        }
    }

    private void submitRequest(String message, EditText editText, User user) {
        if(Utils.isLogged(getContext())) {
            PDFReq.Request request = new PDFReq.Request();
            request.setUser_id(String.valueOf(user.getUserId()));
            request.setRequest(editText.getText().toString());
            try {
                request.setId(mRequests.get(mRequests.size() - 1).getId() + 1);
            }catch (IndexOutOfBoundsException ignored){
                request.setId("0");
            }
            request.setStatus(String.valueOf(PDFReq.PENDING));
            request.setDate(System.currentTimeMillis());
            request.setType(0);
            request.setUser_name(user.getName());
            sendRequest(request);
            mRequests.add(0,request);
            mRecyclerView.scrollToPosition(0);
            mRequestAdapter.notifyItemInserted(0);
            editText.setText("");
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
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Login")
                .customView(R.layout.login_form,true)
                .positiveText("Login")
                .negativeText("Cancel")
                .neutralText("Register")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showRegister(message);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .build();
        dialog.show();
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            View view = dialog.getCustomView();
            MaterialEditText email = (MaterialEditText) view.findViewById(R.id.email);
            MaterialEditText pass = (MaterialEditText) view.findViewById(R.id.pass);
            @Override
            public void onClick(View v) {
                login(dialog,email,pass,message);
            }
        });


    }

    private void login(final MaterialDialog dialog, final MaterialEditText email, final MaterialEditText pass, final String message) {
        final String emailString = email.getText().toString();
        final String passString = pass.getText().toString();
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://mumineendownloads.com/app/login_pdf_app.php";
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "wp":
                                pass.setError("Invalid Pass");
                                break;
                            case "-1":
                                break;
                            default:
                                Gson gson = new Gson();
                                String[] a = gson.fromJson(response, new TypeToken<String[]>() {
                                }.getType());
                                User user1 = new User();
                                user1.setUserId(Integer.parseInt(a[0]));
                                user1.setName(a[1]);
                                user1.setEmail(a[2]);
                                dialog.dismiss();
                                registerLocally(user1.getUserId(), user1.getEmail(), user1.getName());
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
                params.put("pass", passString);
                params.put("email", emailString);
                return params;
            }
        };
        queue.add(stringRequest);

    }

    public void loginLocally(String message){

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
                            Log.e("RR",response);
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
                                        registerLocally(id, email.getText().toString(), name.getText().toString());
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
                    params.put("pass", passwordString);
                    return params;
                }
            };
            queue.add(stringRequest);
        }


    }

    private void registerLocally(int id, String email, String name) {
        Utils.registerUser(name,email,id,getContext());
        PDFReq.Request request = new PDFReq.Request();
        request.setUser_id(String.valueOf(id));
        request.setRequest(editText.getText().toString());
        request.setId(mRequests.get(mRequests.size()-1).getId()+1);
        request.setStatus(String.valueOf(PDFReq.PENDING));
        request.setDate(System.currentTimeMillis());
        request.setType(0);
        sendRequest(request);
        mRequests.add(0,request);
        mRecyclerView.scrollToPosition(0);
        mRequestAdapter.notifyItemInserted(0);
        editText.setText("");
    }

    private void sendRequest(final PDFReq.Request request) {
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("user_id", request.getUser_id());
                params.put("user_name", request.getUser_name());
                params.put("message", request.getRequest());
                params.put("status", request.getStatus());
                params.put("time", String.valueOf(request.getDate()));
                params.put("type", String.valueOf(request.getType()));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void getRequest(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://www.mumineendownloads.com/app/getRequests.php?u="+user.getUserId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response",response);
                        loading.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
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
        mRequestAdapter = new RequestAdapter(mRequests,getContext(),RequestPage.this);
        mRecyclerView.addItemDecoration(new ChatDivider(getContext()));
        mRecyclerView.setAdapter(mRequestAdapter);
    }

    public void setFile(final SelectFile file) {
        new MaterialDialog.Builder(getActivity()).content("Do you want to upload "+file.getFilename() + "?")
                .positiveText("UPLOAD")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        uploadPDF(file);
                    }
                })
                .negativeText("Cancel").build().show();
    }

    private void uploadPDF(SelectFile file) {
        PDFReq.Request request = new PDFReq.Request();
        request.setUser_name(user.getName());
        request.setUser_id(String.valueOf(user.getUserId()));
        request.setDate(System.currentTimeMillis());
        request.setRequest("PDF File Uploaded : " + file.getFilename());
        request.setType(PDFReq.TYPE_PDF);
        request.setStatus(String.valueOf(PDFReq.PENDING));
        sendRequest(request);
        mRequests.add(0,request);
        mRequestAdapter.notifyItemInserted(0);
    }
}
