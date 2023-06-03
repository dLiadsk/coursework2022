package com.example.auction.services;

import com.example.auction.models.Lot;
import com.example.auction.models.User;
import com.example.auction.repositories.LotRepository;
import com.example.auction.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final UserRepository userRepository;

    public List<Lot> listOfLots(String keyWord) {
        if (keyWord != null) {
            List<Lot> lots = lotRepository.findByTitleOfLotContainingIgnoreCaseAndActive(keyWord, true);
            for (Lot lot : lotRepository.findByDescriptionContainingIgnoreCaseAndActive(keyWord, true)){
                if (!lots.contains(lot))lots.add(lot);
            }
            return lots;
        }
        List<Lot> lots = lotRepository.findAllByActive(true);
        lots.sort(Comparator.comparing(Lot::getDateAndTimeOfCreation));
        return lots;
    }

    public void saveLot(Principal principal, Lot lot) {
        lot.setUser(getUserByPrincipal(principal));
        lot.setActive(true);
        lot.setRate(lot.getFirstRate());
        lotRepository.save(lot);
    }

    public void makeRate(double rate, Lot lot, long idOfWinner) {
        lot.setRate(rate);
        lot.setIdOfWinner(idOfWinner);
        lotRepository.save(lot);
    }

    public void editLot(Lot lotCurrent, Lot lotNew) {
        if (!lotNew.getTitleOfLot().equals(lotCurrent.getTitleOfLot())) {
            lotCurrent.setTitleOfLot(lotNew.getTitleOfLot());
        }
        if (!lotNew.getDescription().equals(lotCurrent.getDescription())) {
            lotCurrent.setDescription(lotNew.getDescription());
        }
        double fRate = lotNew.getFirstRate();
        if (fRate != lotCurrent.getFirstRate()) {
            if (fRate < lotCurrent.getFirstRate() && lotCurrent.getFirstRate() == lotCurrent.getRate())
                lotCurrent.setRate(fRate);
            lotCurrent.setFirstRate(lotNew.getFirstRate());
            if (fRate > lotCurrent.getRate()) {
                lotCurrent.setIdOfWinner(0);
                lotCurrent.setRate(fRate);
            }
        }
        lotRepository.save(lotCurrent);
    }

    public void setActivity(Lot lot) {
        lot.setActive(!lot.isActive());
        lotRepository.save(lot);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userRepository.findByEmail(principal.getName());
    }

    public boolean deleteLot(User user, Long id) {
        Lot lot = getLotById(id);

        if (lot != null) {
            if (user.getUserId().equals(lot.getUser().getUserId())) {
                lotRepository.delete(lot);
                return true;
            }
        }
        return false;
    }

    public Lot getLotById(Long id) {
        return lotRepository.findById(id).orElse(null);
    }

    public List<Lot> findMyLots(User user) {
        return lotRepository.findAllByUser(user);
    }


}
