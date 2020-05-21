package com.mickael.go4lunch.ui.map.fragment.workmate;

import android.graphics.Color;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_NAME;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link com.mickael.go4lunch.ui.map.fragment.workmate.WorkmateFragment.OnItemClickListener}.
 */
public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.WorkmateViewHolder> {

    private List<User> users;
    private final WorkmateFragment.OnItemClickListener clickListener;

    WorkmatesListAdapter(WorkmateFragment.OnItemClickListener listener) {
        this.users = new ArrayList<>();
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false));
    }

    @Override
    public void onBindViewHolder(final WorkmateViewHolder holder, int position) {
        User user = this.users.get(position);
        Glide.with(holder.itemView)
                .load(user.getUrlPicture())
                .circleCrop()
                .into(holder.imgUser);

        if (user.getLunchRestaurant() != null) {
            holder.tvLaunchPlaces.setTextColor(Color.BLACK);
            this.updateNoonRestaurantName(user, holder);
        } else {
            holder.tvLaunchPlaces.setTextColor(Color.GRAY);
            holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s hasn't decided yet", user.getUsername()));
        }
        holder.itemView.setOnClickListener(v -> this.clickListener.onClick(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    void updateList(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    private void updateNoonRestaurantName(User user, WorkmateViewHolder holder) {
        if (user.getLunchRestaurant() != null) {
            Map<String, String> restaurantField = user.getLunchRestaurant();
            holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s is eating at %s", user.getUsername(), restaurantField.get(KEY_MAP_RESTAURANT_NAME)));
        } else {
            holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s is eating at unknown restaurant", user.getUsername()));
        }
    }

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
