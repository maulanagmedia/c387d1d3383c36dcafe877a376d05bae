package gmedia.net.id.psplocationfinder.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.psplocationfinder.R;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {

    private Context context;
    private List<Bitmap> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivItem1, ivDelete;

        public MyViewHolder(View view) {

            super(view);

            ivItem1 = (ImageView) view.findViewById(R.id.iv_item1);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }

    public PhotosAdapter(Context context, List masterlist){
        this.context = context;
        this.masterList = masterlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_photos, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Bitmap image = masterList.get(position);

        holder.ivItem1.setImageBitmap(image);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog builder = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus foto?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                masterList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

    public List<Bitmap> getData(){
        return masterList;
    }

}