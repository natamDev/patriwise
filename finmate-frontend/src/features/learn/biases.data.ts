export interface CognitiveBias {
  id: string
  name: string
  icon: string
  definition: string
  example: string
  impact: string
  howToAvoid: string
}

export const biases: CognitiveBias[] = [
  {
    id: 'fomo',
    name: 'FOMO',
    icon: '🚀',
    definition:
      "Le Fear Of Missing Out (peur de rater quelque chose) pousse à investir dans un actif en forte hausse par crainte de passer à côté d'un gain. C'est une décision émotionnelle, pas rationnelle.",
    example:
      "Tu vois une crypto multiplier par 10 en une semaine. Tout le monde en parle. Tu achètes au sommet... et le lendemain elle chute de 60%.",
    impact:
      "Acheter au plus haut, vendre au plus bas. C'est le comportement inverse de ce qu'il faut faire. Le FOMO est responsable de la plupart des pertes des investisseurs débutants.",
    howToAvoid:
      "Attends 48h avant tout achat impulsif. Demande-toi : est-ce que j'aurais voulu acheter cet actif il y a 6 mois ? Si non, c'est du FOMO.",
  },
  {
    id: 'confirmation-bias',
    name: 'Biais de confirmation',
    icon: '🔍',
    definition:
      "On cherche naturellement les informations qui confirment nos convictions et on ignore celles qui les contredisent. En finance, cela mène à sous-estimer les risques d'un investissement qu'on aime déjà.",
    example:
      "Tu es convaincu qu'une action va monter. Tu lis uniquement les analyses positives, tu ignores les avertissements. L'action chute de 40%.",
    impact:
      "Prise de risque excessive, manque de diversification, refus de vendre à perte même quand c'est rationnel.",
    howToAvoid:
      "Cherche activement les arguments contre ton investissement. Lis les analyses négatives en premier. Utilise des données objectives plutôt que des opinions.",
  },
  {
    id: 'overconfidence',
    name: 'Excès de confiance',
    icon: '💪',
    definition:
      "Les humains surestiment systématiquement leurs capacités et leur connaissance des marchés. Après quelques succès, on croit pouvoir battre le marché en permanence.",
    example:
      "Tu as bien prédit 2 hausses. Tu augmentes massivement tes positions. Le marché retourne contre toi et tu perds en quelques jours ce que tu avais gagné en mois.",
    impact:
      "Prises de risque inconsidérées, sous-diversification, trading excessif qui génère des frais et des pertes.",
    howToAvoid:
      "Rappelle-toi que même les meilleurs gérants de fonds ne battent pas l'indice sur 10 ans. Un ETF passif bat 90% des stock-pickers professionnels.",
  },
  {
    id: 'loss-aversion',
    name: 'Aversion aux pertes',
    icon: '😰',
    definition:
      "La douleur d'une perte est psychologiquement 2x plus forte que le plaisir d'un gain équivalent. Cela pousse à vendre trop tôt pour sécuriser les gains, et à garder trop longtemps les positions perdantes.",
    example:
      "Ton ETF baisse de 15% lors d'une correction. La douleur est insupportable, tu vends tout. Deux mois plus tard, il a récupéré et atteint un nouveau record.",
    impact:
      "Vendre au pire moment, rater les rebonds, cristalliser les pertes. C'est le premier ennemi de l'investisseur long terme.",
    howToAvoid:
      "Ne regarde pas ton portefeuille tous les jours. Mets en place des investissements automatiques. Rappelle-toi que les baisses sont temporaires, les gains long terme sont historiquement constants.",
  },
]
