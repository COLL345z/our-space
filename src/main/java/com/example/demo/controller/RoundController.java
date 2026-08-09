package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rounds")
public class RoundController {
    private final RoundRepository roundRepository;
    private final AnswerRepository answerRepository;
    private final QuestionBankRepository questionBankRepository;

    public RoundController(RoundRepository r, AnswerRepository a, QuestionBankRepository q) {
        this.roundRepository = r; this.answerRepository = a; this.questionBankRepository = q;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return roundRepository.findAll().stream().map(this::toDto)
            .sorted((a, b) -> ((Long) b.get("id")).compareTo((Long) a.get("id")))
            .collect(Collectors.toList());
    }

    @PostMapping("/random")
    public Map<String, Object> createRandom(@RequestParam String mode, @RequestParam(required = false) String targetUsername) {
        List<QuestionBank> pool = questionBankRepository.findByMode(mode);
        QuestionBank chosen = pool.get(new Random().nextInt(pool.size()));

        Round round = new Round();
        round.setQuestionText(chosen.getText());
        round.setMode(mode);
        round.setTargetUsername(targetUsername);
        round.setCreatedAt(LocalDate.now());
        Round saved = roundRepository.save(round);
        return toDto(saved);
    }

    @PostMapping("/custom")
    public Map<String, Object> createCustom(@RequestBody Map<String, String> body) {
        Round round = new Round();
        round.setQuestionText(body.get("questionText"));
        round.setMode(body.get("mode"));
        round.setTargetUsername(body.get("targetUsername"));
        round.setCreatedAt(LocalDate.now());
        Round saved = roundRepository.save(round);
        return toDto(saved);
    }

    @PostMapping("/{id}/answer")
    public Map<String, Object> submitAnswer(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // One answer per user per round — overwrite if they already answered
        List<Answer> existing = answerRepository.findByRoundId(id);
        Answer existingForUser = existing.stream()
            .filter(a -> a.getUsername().equalsIgnoreCase(body.get("username")))
            .findFirst().orElse(null);

        Answer answer = existingForUser != null ? existingForUser : new Answer();
        answer.setRoundId(id);
        answer.setUsername(body.get("username"));
        answer.setText(body.get("text"));
        answerRepository.save(answer);

        return toDto(roundRepository.findById(id).orElseThrow());
    }

    @PostMapping("/{id}/mark-guess")
    public Map<String, Object> markGuess(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Round round = roundRepository.findById(id).orElseThrow();
        round.setGuessCorrect(body.get("correct"));
        roundRepository.save(round);
        return toDto(round);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { roundRepository.deleteById(id); }

    private Map<String, Object> toDto(Round round) {
        List<Answer> answers = answerRepository.findByRoundId(round.getId());
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", round.getId());
        dto.put("questionText", round.getQuestionText());
        dto.put("mode", round.getMode());
        dto.put("targetUsername", round.getTargetUsername());
        dto.put("createdAt", round.getCreatedAt().toString());
        dto.put("guessCorrect", round.getGuessCorrect());
        dto.put("answers", answers.stream().map(a -> Map.of("username", a.getUsername(), "text", a.getText())).toList());
        dto.put("isRevealed", answers.size() >= 2); // the lock — only unlocks once BOTH have answered
        return dto;
    }
}
