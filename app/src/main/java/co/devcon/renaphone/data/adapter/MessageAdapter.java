package co.devcon.renaphone.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.devcon.renaphone.R;
import co.devcon.renaphone.data.model.TranslatedMessage;

/**
 * Created by MuhammadIqbal on 1/1/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context mContext;
    private ArrayList<TranslatedMessage> mData;

    public MessageAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vh_chat, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        TranslatedMessage msg = getMessage(position);
        holder.message.setText(msg.getMessage());
        holder.from.setText(msg.getFromSID());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /***
     * Add new Message item into list
     * @param msg
     */
    public void addMessage(TranslatedMessage msg) {
        mData.add(msg);
    }

    /***
     * Get message from index
     * @param index
     * @return
     */
    public TranslatedMessage getMessage(int index) {
        return mData.get(index);
    }

    /**************************************INNER CLASS*****************************************/

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView from;
        TextView message;

        public MessageViewHolder(View itemView) {
            super(itemView);

            from = (TextView) itemView.findViewById(R.id.tv_from);
            message = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
