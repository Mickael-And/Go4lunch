package com.mickael.go4lunch.ui.map.fragment.workmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

import static com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.EXTRAS_RESTAURANT_ID;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.OnItemClickListener}
 * interface.
 */
public class WorkmateFragment extends DaggerFragment {

    @BindView(R.id.list_workmates)
    RecyclerView recyclerView;

    @BindView(R.id.tv_workmates_error_message)
    MaterialTextView tvErrorMessage;

    @Inject
    ViewModelFactory viewModelFactory;

    private WorkmateFragmentViewModel viewModel;

    private WorkmatesListAdapter workmatesListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(this, this.viewModelFactory).get(WorkmateFragmentViewModel.class);
        this.viewModel.getUsers().observe(this, this::manageUsersListe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmate_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.workmatesListAdapter = new WorkmatesListAdapter(user -> {
            if (user.getLunchRestaurant() != null) {
                Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
                intent.putExtra(EXTRAS_RESTAURANT_ID, user.getLunchRestaurant().get(RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_ID));
                startActivity(intent);
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setAdapter(this.workmatesListAdapter);
    }

    private void manageUsersListe(List<User> users) {
        if (users != null && !users.isEmpty()) {
            this.recyclerView.setVisibility(View.VISIBLE);
            this.tvErrorMessage.setVisibility(View.INVISIBLE);
            this.workmatesListAdapter.updateList(users);
        } else {
            this.recyclerView.setVisibility(View.INVISIBLE);
            this.tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onClick(User user);
    }
}
