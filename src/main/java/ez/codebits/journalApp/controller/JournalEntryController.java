package ez.codebits.journalApp.controller;

import ez.codebits.journalApp.entity.JournalEntry;
import ez.codebits.journalApp.entity.User;
import ez.codebits.journalApp.service.JournalEntryService;
import ez.codebits.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
     @Autowired
     private JournalEntryService journalEntryService;
     @Autowired
     private UserService userService;

     @GetMapping
     public ResponseEntity<List<JournalEntry>> getJournalEntriesOfUser(){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         User user = userService.findByUsername(username);
         List<JournalEntry> entries = user.getJournalEntries();
         if(entries != null && !entries.isEmpty())
             return new ResponseEntity<>(entries, HttpStatus.OK);
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }

     @GetMapping("{id}")
     public ResponseEntity<JournalEntry> getById(@PathVariable ObjectId id){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         User user = userService.findByUsername(username);
         List<JournalEntry> userJournalEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
         if(!userJournalEntries.isEmpty()){
             Optional<JournalEntry> entry = journalEntryService.getById(id);
             if(entry.isPresent())
                 return new ResponseEntity<>(entry.get(), HttpStatus.OK);
         }
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }

     @DeleteMapping("{id}")
     public ResponseEntity<?> removeById(@PathVariable ObjectId id){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         boolean isRemoved = journalEntryService.deleteById(id, username);
         if(isRemoved)
             return new ResponseEntity<>(HttpStatus.NO_CONTENT);
         else
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }

     @PostMapping
     public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry journalEntry){
         try {
             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
             String username = authentication.getName();
             journalEntryService.saveEntity(journalEntry, username);
             return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
         }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
     }

     @PutMapping("{id}")
     public ResponseEntity<JournalEntry> updateEntry(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         User user = userService.findByUsername(username);
         List<JournalEntry> userJournalEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
         if(!userJournalEntries.isEmpty()){
             JournalEntry oldEntry = journalEntryService.getById(id).orElse(null);
             if(oldEntry != null){
                 oldEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : oldEntry.getContent());
                 oldEntry.setTitle(!newEntry.getTitle().equals("") ? newEntry.getTitle() : oldEntry.getTitle());
                 journalEntryService.saveEntity(oldEntry);
                 return new ResponseEntity<>(oldEntry, HttpStatus.OK);
             }
         }
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }
}
