package com.mickael.go4lunch.ui.map.fragment.workmate;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.AppRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

/**
 * {@link ViewModel} of {@link WorkmateFragment}.
 */
public class WorkmateFragmentViewModel extends ViewModel {

    /**
     * Users list.
     */
    @Getter
    private MutableLiveData<List<User>> users;

    @Inject
    public WorkmateFragmentViewModel(AppRepository appRepository) {
        this.users = appRepository.getUsers();
    }
}
