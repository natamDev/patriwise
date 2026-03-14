export interface Lesson {
  id: string
  title: string
  category: string
  definition: string
  example: string
  advantages: string[]
  risks: string[]
  summary: string
  chartId?: string
}

export const lessons: Lesson[] = [
  {
    id: 'etf-intro',
    title: "Qu'est-ce qu'un ETF ?",
    category: 'Investissement passif',
    definition:
      "Un ETF (Exchange Traded Fund) est un fonds d'investissement coté en bourse qui réplique la performance d'un indice, comme le CAC 40 ou le MSCI World. En achetant un ETF, tu investis instantanément dans des dizaines ou centaines d'entreprises à la fois.",
    example:
      "Acheter un ETF MSCI World, c'est comme acheter un morceau de 1 600 entreprises mondiales en une seule transaction, pour quelques dizaines d'euros.",
    advantages: [
      "Diversification automatique — un seul achat, des centaines d'entreprises",
      'Frais très bas — généralement 0,1 % à 0,3 % par an',
      'Simplicité — pas besoin de choisir des actions individuelles',
      'Accessible — on peut commencer avec 10 €/mois',
    ],
    risks: [
      'Risque de marché — la valeur peut baisser à court terme',
      'Pas de protection contre les crises globales',
      'Nécessite une vision long terme (5 ans minimum)',
    ],
    summary:
      "Un ETF est l'outil d'investissement idéal pour débuter : simple, diversifié et peu coûteux. L'objectif n'est pas de battre le marché, mais d'en capturer la croissance sur le long terme.",
  },
  {
    id: 'diversification',
    title: 'La diversification',
    category: 'Stratégie',
    definition:
      "La diversification consiste à répartir ses investissements sur plusieurs actifs différents pour réduire le risque global. Si une entreprise fait faillite, elle ne représente qu'une infime partie de ton portefeuille.",
    example:
      "Ne jamais mettre tous ses œufs dans le même panier : investir dans un ETF monde plutôt que dans une seule action Apple ou Total.",
    advantages: [
      'Réduit le risque sans sacrifier le rendement',
      "Protège contre les mauvaises performances d'un secteur",
      'Permet de dormir sereinement même en période de turbulence',
    ],
    risks: [
      'Une diversification excessive peut diluer les gains',
      'Ne protège pas contre les crises systémiques mondiales',
    ],
    summary:
      "La diversification est la seule stratégie gratuite en investissement. Un ETF monde offre une diversification immédiate sur des milliers d'entreprises.",
  },
  {
    id: 'long-term',
    title: "L'investissement long terme",
    category: 'Stratégie',
    definition:
      "Investir sur le long terme signifie conserver ses placements pendant plusieurs années (5 à 30 ans) sans céder à la panique lors des baisses temporaires du marché.",
    example:
      "Le marché mondial a connu des crises en 2000, 2008 et 2020, mais s'est à chaque fois redressé pour atteindre de nouveaux sommets. Ceux qui ont gardé leurs ETF ont été récompensés.",
    advantages: [
      'Les intérêts composés multiplient ton capital avec le temps',
      'Les fluctuations à court terme deviennent insignifiantes',
      'Moins de stress et de décisions émotionnelles',
    ],
    risks: [
      "L'argent est immobilisé pendant plusieurs années",
      'Nécessite de la discipline pour ne pas vendre en période de panique',
    ],
    summary:
      "Le temps est ton meilleur allié en investissement. Chaque année supplémentaire d'investissement peut doubler l'impact des intérêts composés sur ton capital final.",
  },
  {
    id: 'volatility',
    title: 'Comprendre la volatilité',
    category: 'Gestion du risque',
    chartId: 'volatility',
    definition:
      "La volatilité mesure l'amplitude des variations de prix d'un actif. Un marché volatile monte et descend fortement à court terme, mais cela ne signifie pas qu'il ne progresse pas sur le long terme.",
    example:
      "Le marché actions mondial a chuté de -34% en mars 2020 (Covid), puis a atteint un nouveau record historique 5 mois plus tard. Ceux qui ont vendu dans la panique ont raté le rebond.",
    advantages: [
      'La volatilité crée des opportunités pour les investisseurs patients',
      "Investir régulièrement (DCA) permet de profiter des baisses en achetant à prix réduit",
      'Sur 20 ans, les marchés ont toujours fini plus haut malgré les crises',
    ],
    risks: [
      'La volatilité peut pousser à vendre au mauvais moment par peur',
      'Les actifs très volatils peuvent mettre des années à récupérer',
      'Ne pas confondre volatilité temporaire et perte permanente',
    ],
    summary:
      "La volatilité est normale et inévitable. La vraie erreur n'est pas de subir les baisses, mais de vendre sous l'effet de la panique. Un investisseur discipliné voit les corrections comme des opportunités.",
  },
]
