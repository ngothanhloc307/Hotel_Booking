package com.example.backend.services;

import com.example.backend.dtos.LoginRequest;
import com.example.backend.dtos.RegistrationRequest;
import com.example.backend.dtos.Response;
import com.example.backend.dtos.UserDTO;
import com.example.backend.entities.User;

public interface UserService {

    Response registerUser(RegistrationRequest registrationRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    Response getOwnAccountDetails();

    User getCurrentLoggedInUser();

    Response updateOwnAccount(UserDTO userDTO);

    Response deleteOwnAccount();

    Response getMyBookingHistory();


}
