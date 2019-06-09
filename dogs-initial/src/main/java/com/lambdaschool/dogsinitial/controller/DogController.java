package com.lambdaschool.dogsinitial.controller;

import com.lambdaschool.dogsinitial.exception.ResourceNotFoundException;
import com.lambdaschool.dogsinitial.model.Dog;
import com.lambdaschool.dogsinitial.DogsinitialApplication;
import com.lambdaschool.dogsinitial.model.MessageDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/dogs")
public class DogController
{
    private static final Logger logger = LoggerFactory.getLogger(DogController.class);

    @Autowired
    RabbitTemplate rt;

    // localhost:8080/dogs/dogs
    @GetMapping(value = "/dogs")
    public ResponseEntity<?> getAllDogs()
    {
        logger.info("/dogs/dogs accessed at " + new Date() + " in HIGH QUEUE");
        MessageDetail message = new MessageDetail("/dogs/dogs accessed", 7, false, new Date() + "", "None");
        rt.convertAndSend(DogsinitialApplication.QUEUE_NAME_HIGH, message);
        return new ResponseEntity<>(DogsinitialApplication.ourDogList.dogList, HttpStatus.OK);
    }

    // localhost:8080/dogs/{id}
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDogDetail(@PathVariable long id)
    {
        logger.trace("/dogs/{id} accessed with id: " + id + " at " + new Date() + " in LOW QUEUE");
        MessageDetail message = new MessageDetail("/dogs/{id} accessed", 7, false, new Date() + "", id + "");
        rt.convertAndSend(DogsinitialApplication.QUEUE_NAME_LOW, message);
        Dog rtnDog = DogsinitialApplication.ourDogList.findDog(d -> (d.getId() == id));
        if (rtnDog == null)
        {
            throw new ResourceNotFoundException("The dog with id " + id + " could not be found.");
        }
        else
        {
            return new ResponseEntity<>(rtnDog, HttpStatus.OK);
        }
    }

    // localhost:8080/dogs/breeds/{breed}
    @GetMapping(value = "/breeds/{breed}")
    public ResponseEntity<?> getDogBreeds (@PathVariable String breed)
    {
        ArrayList<Dog> rtnDogs = DogsinitialApplication.ourDogList.
                findDogs(d -> d.getBreed().toUpperCase().equals(breed.toUpperCase()));
        if (rtnDogs.size() == 0)
        {
            throw new ResourceNotFoundException("No dogs of breed " + breed + " were found.");
        }
        else
        {
            return new ResponseEntity<>(rtnDogs, HttpStatus.OK);
        }
    }

    // Display all dogs ordered by breed
    @GetMapping(value = "/breedstable")
    public ModelAndView getDogsByBreeds()
    {
        ModelAndView mav = new ModelAndView();
        DogsinitialApplication.ourDogList.dogList.sort((d1, d2) -> d1.getBreed().compareToIgnoreCase(d2.getBreed()));
        mav.setViewName("dogs");
        mav.addObject("dogList", DogsinitialApplication.ourDogList.dogList);

        return mav;
    }

    // Display all dogs suitable for apartments ordered by breed
    @GetMapping(value = "/apartmentbreedstable")
    public ModelAndView getApartmentBreeds()
    {
        ModelAndView mav = new ModelAndView();
        DogsinitialApplication.ourDogList.dogList.sort((d1, d2) -> d1.getBreed().compareToIgnoreCase(d2.getBreed()));
        ArrayList<Dog> apartmentDogs = new ArrayList<>();
        for (Dog d : DogsinitialApplication.ourDogList.dogList)
        {
            if (d.isApartmentSuitable())
            {
                apartmentDogs.add(d);
            }
        }
        mav.setViewName("dogs");
        mav.addObject("dogList", apartmentDogs);

        return mav;
    }
}
