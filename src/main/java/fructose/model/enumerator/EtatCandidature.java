package fructose.model.enumerator;

public enum EtatCandidature {
	// Phase de candidature initiale
	EN_ATTENTE,                                                    // Nouvelle candidature en attente de traitement par l'employeur
	
	// Phase de décision de l'employeur
	REFUSEE,                                                           // Candidature refusée par l'employeur avant entrevue
	ENTREVUE_PROPOSE,                                     // Candidature approuvée, et doit accepter ou rejeter la Date d'Entrevue Porposé
	
	// Phase de décision de l'étudiant
	ENTREVUE_ACCEPTE_ETUDIANT,                // En entrevue avec l'employeur
	ENTREVUE_REFUSE_ETUDIANT,                  // Étudiant refuse de poursuivre ce stage car il aime pas la date d'Entrevue
	
	// Phase d'après l'entrevue
	ACCEPTE_APRES_ENTREVUE,                         // Offre accepte par l'employeur après entrevue
	REFUSEE_APRES_ENTREVUE,                         // Offre refusée par l'étudiant après entrevue
	
	// Phase de signature de contrat
	CONTRAT_CREE_PAR_GESTIONNAIRE,      // Contrat créé par le gestionnaire
	POSTE_OBTENU          				          // Contrat signé par tous les parties
}