package xyz.jorjyeah.skripsi.invicikey;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder>{
    private ArrayList<KeyListModel> dataList;
    private Context context;

    public CredentialAdapter(ArrayList<KeyListModel> dataList){ this.dataList = dataList; }

    @Override
    public CredentialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        context = parent.getContext();
        View view = layoutInflater.inflate(R.layout.card_view_layout,parent,false);
        return new CredentialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CredentialViewHolder holder, final int position) {
        holder.txtUsername.setText(dataList.get(position).getUsername());
        holder.txtAppid.setText(dataList.get(position).getAppid());
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyhandle = dataList.get(position).getKeyhandle();
                int id = dataList.get(position).getId();
                Toast.makeText(context, keyhandle+id, Toast.LENGTH_SHORT).show();
                CredentialDBHelper credentialDBHelper = new CredentialDBHelper(context);
                credentialDBHelper.deleteCredential(keyhandle,id,context);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList!=null)?dataList.size() : 0;
    }


    public class CredentialViewHolder extends  RecyclerView.ViewHolder{
        private TextView txtUsername, txtAppid;
        private Button delButton;
        public CredentialViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_username);
            txtAppid = itemView.findViewById(R.id.txt_appid);
            delButton = itemView.findViewById(R.id.deletebutton);
        }
    }

//    private CredentialCallback callback;
//    public void setCallback(CredentialCallback callback){
//        this.callback = callback;
//    };
}
