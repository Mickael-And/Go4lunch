package com.mickael.go4lunch.ui.map.fragment.workmate;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

public class WorkmateFragmentViewModel extends ViewModel {

    @Getter
    private MutableLiveData<List<User>> users;

    @Inject
    public WorkmateFragmentViewModel(UserRepository userRepository) {
        this.users = userRepository.getUsers();
    }
}
