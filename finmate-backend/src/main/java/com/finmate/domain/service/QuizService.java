package com.finmate.domain.service;

import com.finmate.domain.model.Quiz;
import com.finmate.domain.model.QuizResult;
import com.finmate.domain.port.QuizRepository;
import com.finmate.domain.port.QuizResultRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;

    public QuizService(QuizRepository quizRepository, QuizResultRepository quizResultRepository) {
        this.quizRepository = quizRepository;
        this.quizResultRepository = quizResultRepository;
    }

    void onStart(@Observes StartupEvent event) {
        if (quizRepository.count() == 0) {
            seed();
        }
    }

    public record QuizDto(UUID id, String question, List<String> options) {}
    public record AnswerResult(boolean correct, String explanation, int correctAnswer) {}
    public record UserStats(int answered, int correct) {}

    public List<QuizDto> getAll(UUID userId) {
        return quizRepository.findAll().stream()
                .map(q -> new QuizDto(q.getId(), q.getQuestion(), q.getOptions()))
                .toList();
    }

    public AnswerResult answer(UUID userId, UUID quizId, int selectedOption) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz introuvable."));

        boolean correct = selectedOption == quiz.getCorrectAnswer();

        // Save result (allow re-taking)
        QuizResult result = new QuizResult();
        result.setUserId(userId);
        result.setQuizId(quizId);
        result.setScore(correct ? 1 : 0);
        result.setAnsweredAt(LocalDateTime.now());
        quizResultRepository.save(result);

        return new AnswerResult(correct, quiz.getExplanation(), quiz.getCorrectAnswer());
    }

    public UserStats getStats(UUID userId) {
        List<QuizResult> results = quizResultRepository.findByUserId(userId);
        int correct = (int) results.stream().filter(r -> r.getScore() == 1).count();
        return new UserStats(results.size(), correct);
    }

    private void seed() {
        List<Quiz> quizzes = List.of(
                new Quiz(null,
                        "Qu'est-ce qu'un ETF (Exchange Traded Fund) ?",
                        List.of(
                                "Un prêt bancaire à taux fixe",
                                "Un fonds coté en bourse qui réplique un indice",
                                "Une assurance vie classique",
                                "Un compte épargne réglementé"
                        ),
                        1,
                        "Un ETF est un fonds d'investissement coté en bourse. Il réplique la performance d'un indice (ex : S&P 500) et permet de diversifier facilement avec de faibles frais."
                ),
                new Quiz(null,
                        "Que signifie DCA (Dollar Cost Averaging) ?",
                        List.of(
                                "Investir toutes ses économies en une seule fois",
                                "Acheter uniquement quand les marchés baissent",
                                "Investir un montant fixe à intervalles réguliers",
                                "Diversifier dans plusieurs devises étrangères"
                        ),
                        2,
                        "Le DCA consiste à investir un montant fixe régulièrement (ex : 100 € par mois). Cela lisse le prix d'achat moyen et réduit le risque de mal timer le marché."
                ),
                new Quiz(null,
                        "Quel est le principal avantage des intérêts composés ?",
                        List.of(
                                "Ils garantissent un rendement minimum chaque année",
                                "Ils permettent de ne jamais perdre d'argent",
                                "Les gains génèrent eux-mêmes des gains sur le long terme",
                                "Ils s'appliquent uniquement aux comptes bancaires"
                        ),
                        2,
                        "Les intérêts composés font que tes gains génèrent eux-mêmes des gains. Sur le long terme, cet effet exponentiel transforme une petite épargne régulière en capital significatif."
                ),
                new Quiz(null,
                        "Pourquoi la diversification est-elle importante ?",
                        List.of(
                                "Elle garantit des rendements plus élevés",
                                "Elle élimine totalement le risque d'investissement",
                                "Elle réduit le risque en répartissant les investissements",
                                "Elle permet de payer moins d'impôts"
                        ),
                        2,
                        "La diversification réduit le risque en évitant de tout miser sur un seul actif. Si une position chute, les autres peuvent compenser. Un ETF Monde diversifie automatiquement sur des centaines d'entreprises."
                ),
                new Quiz(null,
                        "Qu'est-ce que le biais FOMO en investissement ?",
                        List.of(
                                "Une stratégie d'achat sur les marchés émergents",
                                "La peur de rater une opportunité, menant à des décisions impulsives",
                                "Un indicateur technique de surachat",
                                "Un fonds obligataire à rendement garanti"
                        ),
                        1,
                        "Le FOMO (Fear Of Missing Out) pousse à acheter un actif en forte hausse par peur de rater un gain. C'est une décision émotionnelle qui mène souvent à acheter au plus haut et vendre au plus bas."
                )
        );
        quizzes.forEach(quizRepository::save);
    }
}
