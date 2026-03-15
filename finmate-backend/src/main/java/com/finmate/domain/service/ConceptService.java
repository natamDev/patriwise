package com.finmate.domain.service;

import com.finmate.domain.model.ConceptCard;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ConceptService {

    private static final Map<String, ConceptCard> CONCEPTS = Map.ofEntries(
            Map.entry("etf", new ConceptCard(
                    "ETF (Exchange Traded Fund)",
                    "Un ETF est un fonds d'investissement coté en bourse qui réplique la performance d'un indice (ex : S&P 500, CAC 40). En achetant une seule part, tu détiens indirectement des centaines d'entreprises.",
                    "L'ETF MSCI World contient plus de 1 500 entreprises dans 23 pays. Avec 100 €, tu es exposé à Apple, LVMH, Toyota et des centaines d'autres.",
                    "La valeur d'un ETF peut baisser si l'indice qu'il réplique baisse. En cas de crise mondiale, tous les actifs de l'ETF peuvent chuter en même temps.",
                    "Un ETF = diversification automatique, frais faibles, accessible dès 1 €. C'est le produit préféré des investisseurs passifs."
            )),
            Map.entry("volatilite", new ConceptCard(
                    "Volatilité",
                    "La volatilité mesure l'amplitude des variations de prix d'un actif. Un actif très volatil monte et descend fortement en peu de temps.",
                    "Le Bitcoin peut gagner ou perdre 20 % en une semaine. Un ETF Monde varie rarement de plus de 3 % par semaine.",
                    "Une forte volatilité peut pousser à vendre au pire moment (lors d'une chute), cristallisant des pertes qui auraient été temporaires.",
                    "Volatilité élevée = plus de risque, mais aussi plus d'opportunités. Pour un investisseur long terme, la volatilité courte est du bruit à ignorer."
            )),
            Map.entry("diversification", new ConceptCard(
                    "Diversification",
                    "Répartir son capital sur plusieurs actifs différents (secteurs, pays, types) pour réduire le risque global. Si un actif chute, les autres compensent.",
                    "Un portefeuille avec 100 % d'actions d'une seule entreprise est très risqué. Si cette entreprise fait faillite, tu perds tout. Avec un ETF Monde, la faillite d'une entreprise n'a presque aucun impact.",
                    "La diversification réduit le risque mais ne l'élimine pas. En cas de crise systémique (2008, 2020), tous les actifs corrélés peuvent baisser ensemble.",
                    "Ne pas mettre tous ses oeufs dans le même panier. Un ETF Monde diversifie automatiquement sur des centaines d'entreprises."
            )),
            Map.entry("interets-composes", new ConceptCard(
                    "Intérêts composés",
                    "Les intérêts composés, c'est le fait que tes gains génèrent eux-mêmes des gains. Chaque année, tu gagnes des intérêts non seulement sur ton capital initial, mais aussi sur les intérêts déjà accumulés.",
                    "Tu investis 1 000 € à 7 % par an. Après 1 an : 1 070 €. Après 2 ans : 1 144,9 €. Après 30 ans : 7 612 € — sans jamais rajouter un centime.",
                    "Les intérêts composés fonctionnent aussi à l'envers : les dettes à taux élevé (crédit renouvelable) s'accumulent de la même façon contre toi.",
                    "Plus tu commences tôt, plus l'effet est puissant. Le temps est ton meilleur allié en investissement."
            )),
            Map.entry("dca", new ConceptCard(
                    "DCA — Dollar Cost Averaging",
                    "Stratégie qui consiste à investir un montant fixe à intervalles réguliers (ex : 100 € par mois), peu importe le cours du marché.",
                    "Tu investis 100 € chaque mois dans un ETF. Quand le marché est bas, tu achètes plus de parts. Quand il est haut, tu en achètes moins. Sur le long terme, ton prix moyen d'achat se lisse.",
                    "Le DCA ne garantit pas de profit. En marché baissier prolongé, tu accumules des pertes papier pendant une longue période.",
                    "Investir régulièrement sans essayer de timer le marché. C'est la stratégie la plus simple et la plus efficace pour un débutant."
            )),
            Map.entry("action", new ConceptCard(
                    "Action",
                    "Une action représente une part de propriété dans une entreprise. En achetant une action, tu deviens actionnaire et tu participes aux profits (dividendes) et aux pertes de l'entreprise.",
                    "Si tu achètes 1 action Apple à 200 $, tu possèdes une infime fraction d'Apple. Si Apple double ses profits, la valeur de ton action peut augmenter.",
                    "Une action peut perdre toute sa valeur si l'entreprise fait faillite. Concentrer ses investissements sur une seule action est très risqué.",
                    "Acheter des actions = devenir co-propriétaire d'une entreprise. Le risque est élevé sur une seule action, faible sur un panier diversifié (ETF)."
            )),
            Map.entry("inflation", new ConceptCard(
                    "Inflation",
                    "L'inflation est la hausse générale des prix dans le temps. Elle érode le pouvoir d'achat de l'argent qui dort sur un compte sans rendement.",
                    "Avec 2 % d'inflation par an, 1 000 € aujourd'hui n'auront plus que le pouvoir d'achat de 820 € dans 10 ans si tu ne les investis pas.",
                    "Une inflation trop forte peut réduire les bénéfices réels de tes investissements. Si ton ETF gagne 5 % mais l'inflation est à 6 %, tu perds en pouvoir d'achat.",
                    "L'inflation est l'ennemi de l'argent qui ne travaille pas. Investir est le meilleur moyen de préserver et faire croître son pouvoir d'achat."
            )),
            Map.entry("dividende", new ConceptCard(
                    "Dividende",
                    "Un dividende est une part des bénéfices qu'une entreprise redistribue à ses actionnaires, généralement chaque trimestre ou chaque année.",
                    "Une entreprise réalise 1 milliard de bénéfices et décide d'en redistribuer 30 % aux actionnaires. Si tu possèdes 0,001 % des actions, tu reçois 3 000 €.",
                    "Une entreprise peut réduire ou supprimer son dividende à tout moment, surtout en période de crise. Se fier uniquement aux dividendes est risqué.",
                    "Les dividendes sont un revenu passif versé par les entreprises. Réinvestis automatiquement, ils amplifient l'effet des intérêts composés."
            ))
    );

    private static final List<Map.Entry<String, String>> KEYWORD_MAP = List.of(
            Map.entry("etf", "etf"),
            Map.entry("tracker", "etf"),
            Map.entry("fonds indiciel", "etf"),
            Map.entry("volatil", "volatilite"),
            Map.entry("volatilité", "volatilite"),
            Map.entry("diversif", "diversification"),
            Map.entry("intérêt composé", "interets-composes"),
            Map.entry("intérêts composés", "interets-composes"),
            Map.entry("compound", "interets-composes"),
            Map.entry("capitalisation", "interets-composes"),
            Map.entry("dca", "dca"),
            Map.entry("dollar cost", "dca"),
            Map.entry("investissement régulier", "dca"),
            Map.entry("action", "action"),
            Map.entry("actionnaire", "action"),
            Map.entry("inflation", "inflation"),
            Map.entry("pouvoir d'achat", "inflation"),
            Map.entry("dividende", "dividende"),
            Map.entry("dividendes", "dividende")
    );

    public Optional<ConceptCard> detect(String userMessage) {
        String lower = userMessage.toLowerCase();
        for (Map.Entry<String, String> entry : KEYWORD_MAP) {
            if (lower.contains(entry.getKey())) {
                return Optional.ofNullable(CONCEPTS.get(entry.getValue()));
            }
        }
        return Optional.empty();
    }
}
