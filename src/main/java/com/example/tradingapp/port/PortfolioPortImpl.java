package com.example.tradingapp.port;

import com.example.tradingapp.exception.NotFoundException;
import com.example.tradingapp.model.Portfolio;
import com.example.tradingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PortfolioPortImpl implements PortfolioPort {

    private final UserRepository userRepository;

    @Override
    public List<Portfolio> getUserPortfolio(String userId) {
        var userOpt = userRepository.getUser(UUID.fromString(userId));
        try {
            return userOpt.orElseThrow(() -> new NotFoundException(
                            String.format("User with userId %s not found", userId)))
                    .getPortfolio();
        } catch (NotFoundException e) {
            log.info("Given user with id {} does not exist", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
