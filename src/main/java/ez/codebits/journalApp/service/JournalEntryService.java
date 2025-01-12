package ez.codebits.journalApp.service;

import ez.codebits.journalApp.entity.JournalEntry;
import ez.codebits.journalApp.entity.User;
import ez.codebits.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntity(JournalEntry journalEntry, String username){
        try {
            User user = userService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        }catch (Exception e){
            log.error("Exception ",e);
            throw new RuntimeException("An error occurred while saving the entry", e);
        }
    }
    public void saveEntity(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }
    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }
    public Optional<JournalEntry> getById(ObjectId id){
        return journalEntryRepository.findById(id);
    }
    @Transactional
    public boolean deleteById(ObjectId id, String username){
        boolean isRemoved = false;
        try {
            User user = userService.findByUsername(username);
            isRemoved = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if(isRemoved){
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        }catch (Exception e){
            log.error("Exception ",e);
            throw new RuntimeException("An error occurred while deleting the entry", e);
        }
        return isRemoved;
    }
}
