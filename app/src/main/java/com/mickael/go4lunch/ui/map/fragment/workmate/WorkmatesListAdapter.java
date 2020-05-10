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
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.dao.LunchFirestoreDAO;
import com.mickael.go4lunch.data.model.Lunch;
import com.mickael.go4lunch.data.model.User;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link com.mickael.go4lunch.ui.map.fragment.workmate.WorkmateFragment.OnItemClickListener}.
 */
public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.WorkmateViewHolder> {

    private List<User> users;
    private final WorkmateFragment.OnItemClickListener clickListener;

    public WorkmatesListAdapter(List<User> users, WorkmateFragment.OnItemClickListener listener) {
        this.users = users;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_workmate, parent, false));
    }

    @Override
    public void onBindViewHolder(final WorkmateViewHolder holder, int position) {
        User user = this.users.get(position);
        Glide.with(holder.itemView)
                .load(user.getUrlPicture())
                .circleCrop()
                .into(holder.imgUser);

        this.isEatingLunchAtNoon(user, holder);

        holder.itemView.setOnClickListener(v -> this.clickListener.onClick(user));
    }

    private void isEatingLunchAtNoon(User user, WorkmateViewHolder holder) {
        if (user.getLunchId() != null) {
            LunchFirestoreDAO.getLunch(user.getLunchId()).addOnSuccessListener(documentSnapshot -> {
                boolean isEating;

                Lunch userLunch = documentSnapshot.toObject(Lunch.class);

                Date todayReferenceHour = new Date();
                todayReferenceHour.setHours(14);
                todayReferenceHour.setMinutes(0);

                Date dateNow = new Date();

                if (userLunch.getLunchDate().before(dateNow)) {
                    if (dateNow.after(todayReferenceHour)) {
                        isEating = userLunch.getLunchDate().after(todayReferenceHour);
                    } else {
                        todayReferenceHour.setDate(todayReferenceHour.getDate() - 1);
                        isEating = userLunch.getLunchDate().after(todayReferenceHour);
                    }
                } else {
                    isEating = false;
                    Log.w(this.getClass().getSimpleName(), "The day of user launch is after the date now");
                }
                if (isEating) {
                    holder.tvLaunchPlaces.setTextColor(Color.BLACK);
                    holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s is eating at %s", user.getUsername(), user.getLunchId())); // TODO: change user.getLunchId() with restaurant name
                } else {
                    holder.tvLaunchPlaces.setTextColor(Color.GRAY);
                    holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s hasn't decided yet", user.getUsername()));
                }

            });
        } else {
            holder.tvLaunchPlaces.setTextColor(Color.GRAY);
            holder.tvLaunchPlaces.setText(String.format(Locale.getDefault(), "%s hasn't decided yet", user.getUsername()));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateList(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
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
