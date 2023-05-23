package com.example.auction.services;

import com.example.auction.models.Image;
import com.example.auction.models.Lot;
import com.example.auction.models.User;
import com.example.auction.repositories.LotRepository;
import com.example.auction.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final UserRepository userRepository;

    public List<Lot> listOfLots(String keyWord) {
        if (keyWord != null) {
            List<Lot> lots = lotRepository.findByDescriptionContainingIgnoreCaseAndActive(keyWord, true);
            lots.sort(Comparator.comparing(Lot::getDateAndTimeOfCreation));
            lotRepository.findByDescriptionContainingIgnoreCaseAndActive(keyWord, false).stream()
                    .sorted(Comparator.comparing(Lot::getDateAndTimeOfCreation))
                    .forEach(lots::add);
            return lots;
        }
        List<Lot> lots = lotRepository.findAllByActive(true);
        lots.sort(Comparator.comparing(Lot::getDateAndTimeOfCreation));
        lotRepository.findAllByActive(false).stream()
                .sorted(Comparator.comparing(Lot::getDateAndTimeOfCreation))
                .forEach(lots::add);
        return lots;
    }

    public void saveLot(Principal principal, Lot lot, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        lot.setUser(getUserByPrincipal(principal));
        lot.setActive(true);
        lot.setRate(lot.getFirstRate());
        Image image1;
        Image image2;
        Image image3;
        if (file1.getSize() != 0) {
            image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            image1.setOrder(1);
            lot.addImageToLot(image1);
        }
        if (file2.getSize() != 0) {
            image2 = toImageEntity(file2);
            image2.setOrder(2);
            lot.addImageToLot(image2);
        }
        if (file3.getSize() != 0) {
            image3 = toImageEntity(file3);
            image3.setOrder(3);
            lot.addImageToLot(image3);
        }
        Lot lot1 = lotRepository.save(lot);
        lot1.setPreviewImageId(lot1.getImageList().get(0).getId());
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

    Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
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
