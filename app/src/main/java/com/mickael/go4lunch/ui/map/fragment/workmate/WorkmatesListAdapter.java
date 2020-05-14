package com.mickael.go4lunch.ui.map.fragment.workmate;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link com.mickael.go4lunch.ui.map.fragment.workmate.WorkmateFragment.OnItemClickListener}.
 */
public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.WorkmateViewHolder> {

    private static final String TAG = WorkmatesListAdapter.class.getSimpleName();

    private List<User> users;
    private final WorkmateFragment.OnItemClickListener clickListener;
    private final WorkmateFragmentViewModel viewModel;

    WorkmatesListAdapter(WorkmateFragmentViewModel viewModel, List<User> users, WorkmateFragment.OnItemClickListener listener) {
        this.users = users;
        this.clickListener = listener;
        this.viewModel = viewModel;
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

        if (this.viewModel.isEatingLunchAtNoon(user)) {
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
        this.viewModel.getRestaurant(user.getLunchplaceId()).addOnCompleteListener(task -> {
            String restaurantName;
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                    if (restaurant != null) {
                        restaurantName = restaurant.getName();
                    } else {
                        Log.d(TAG, "Response is null");
                        restaurantName = "unknown restaurant";
                    }
                } else {
                    restaurantName = "unknown restaurant";
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                restaurantName = "unknown restaurant";
            }
            holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s is eating at %s", user.getUsername(), restaurantName));
        });
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
