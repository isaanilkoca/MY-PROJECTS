package com.isaanilkoca.javainstagramclone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.isaanilkoca.javainstagramclone.databinding.RecyclerRowBinding;
import com.isaanilkoca.javainstagramclone.model.Post;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList; // Gönderideki verileri tutar

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList; // Gönderi listesini alır
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Görünüm oluştururuz
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(inflater, parent, false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        // Verileri görünüme bağla
        holder.recyclerRowBinding.recylerViewUserEmailText.setText(postArrayList.get(position).email);
        holder.recyclerRowBinding.recylerViewCommentText.setText(postArrayList.get(position).comment);

        // Resimlerin görünmesi için gereken sınıf ve işlemler
        Picasso.get().load(postArrayList.get(position).downloadurl).into(holder.recyclerRowBinding.recylerViewImageView);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size(); // Gönderi sayısını döner
    }

    class PostHolder extends RecyclerView.ViewHolder {
        // Görünüm bileşenlerine erişimi sağlarız
        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
}