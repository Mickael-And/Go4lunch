package com.mickael.go4lunch.ui.restaurantdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User}.
 */
public class WorkmatesDetailsAdapter extends RecyclerView.Adapter<WorkmatesDetailsAdapter.WorkmateViewHolder> {

    /**
     * Users list to display.
     */
    private List<User> users;

    WorkmatesDetailsAdapter() {
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public WorkmatesDetailsAdapter.WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesDetailsAdapter.WorkmateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesDetailsAdapter.WorkmateViewHolder holder, int position) {
        User user = this.users.get(position);
        Glide.with(holder.itemView)
                .load(user.getUrlPicture())
                .circleCrop()
                .into(holder.imgUser);

        holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s is joining!", user.getUsername()));
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    /**
     * Updates the list of users to display.
     *
     * @param users users to display
     */
    void updateList(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder containing an item.
     */
    public static class WorkmateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_user)
        ImageView imgUser;

        @BindView(R.id.tv_launch_places)
        MaterialTextView tvLaunchPlaces;

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
