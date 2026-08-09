package com.example.demo.config;

import com.example.demo.entity.QuestionBank;
import com.example.demo.repository.QuestionBankRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionSeeder {
    @Bean
    CommandLineRunner seedQuestions(QuestionBankRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            String[] shareQuestions = {
                "What's your favorite childhood memory?",
                "What's a small thing that makes you really happy?",
                "What does your perfect lazy Sunday look like?",
                "What's something you've always wanted to learn?",
                "What's your ultimate comfort food?",
                "What's a place you've never been but really want to visit?",
                "What's your love language?",
                "What's the last thing that made you laugh really hard?",
                "What's a fear you don't talk about much?",
                "What's your favorite way to be comforted when you're sad?",
                "What's a song that instantly changes your mood?",
                "What's something you're secretly proud of?",
                "What's your idea of the perfect date night?",
                "What's a habit you're trying to build or break?",
                "What's the best gift you've ever received?",
                "What's something that always makes you cry (happy or sad)?",
                "What's your favorite way to spend a rainy day?",
                "What's a skill you wish you had?",
                "What's your go-to karaoke song?",
                "What's something you believed as a kid that turned out to be wrong?",
                "What's your favorite smell in the world?",
                "What's a memory with me that you think about often?",
                "What's something you want to do together this year?",
                "What's your definition of a perfect home?",
                "What's a book, show, or movie that changed how you see things?"
            };

            String[] guessQuestions = {
                "What's my favorite movie of all time?",
                "What's my biggest pet peeve?",
                "What's the first thing I noticed about you?",
                "What's my go-to order at a coffee shop?",
                "What was I like as a kid?",
                "What's my most-used emoji?",
                "What's something I say way too often?",
                "What's my favorite season and why?",
                "What's the weirdest food combination I actually like?",
                "What's my biggest guilty pleasure?",
                "What's the one chore I hate the most?",
                "What's my dream job if money wasn't a factor?",
                "What's my go-to comfort show to rewatch?",
                "What was my first impression of you?",
                "What's something I'm irrationally afraid of?",
                "What's my favorite thing about myself?",
                "What's my ideal way to spend a birthday?",
                "What's the last thing I texted you about that wasn't about us?",
                "What's my biggest strength according to me?",
                "What's a hill I'd die on in an argument?",
                "What's my favorite thing you do for me?",
                "What's something on my bucket list?",
                "What's my most annoying morning habit?",
                "What's the one meal I could eat every day?",
                "What's something about me that surprised you when we first met?"
            };

            for (String q : shareQuestions) {
                QuestionBank qb = new QuestionBank();
                qb.setText(q);
                qb.setMode("SHARE");
                repo.save(qb);
            }
            for (String q : guessQuestions) {
                QuestionBank qb = new QuestionBank();
                qb.setText(q);
                qb.setMode("GUESS");
                repo.save(qb);
            }
        };
    }
}
