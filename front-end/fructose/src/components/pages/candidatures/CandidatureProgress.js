import React, {useContext} from "react";
import {AuthContext} from "../../providers/AuthProvider";
import {useTranslation} from "react-i18next";
import Icon from "@mdi/react";
import {mdiBriefcaseVariantOutline, mdiCheckCircleOutline, mdiFileSign, mdiForumOutline, mdiSendOutline} from "@mdi/js";

const CandidatureProgress = ({candidature}) => {
	
	const {t} = useTranslation();
	const {currentUser} = useContext(AuthContext);
	
	const etats = [
		"EN_ATTENTE",
		"REFUSEE",
		"ENTREVUE_PROPOSE",
		"ENTREVUE_REFUSE_ETUDIANT",
		"ENTREVUE_ACCEPTE_ETUDIANT",
		"REFUSEE_APRES_ENTREVUE",
		"ACCEPTE_APRES_ENTREVUE",
		"CONTRAT_CREE_PAR_GESTIONNAIRE",
		"POSTE_OBTENU",
	]
	
	let indexEtat = etats.indexOf(candidature.etat)
	
	const getEtatColor = (step) => {
		let etat = candidature.etat
		if (step === 1) {
			if (etat === "POSTE_OBTENU") {
				return "bg-green"
			} else if (etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" && (candidature.signRefuse && candidature.signRefuse === true)){
				return "bg-red"
			} else if (indexEtat === 1 || indexEtat === 3 || indexEtat === 5) {
				return "bg-red"
			} else if (indexEtat >= 0) {
				return "bg-green"
			}
		} else if (step === 2) {
			if (etat === "POSTE_OBTENU") {
				return "bg-green"
			} else if (etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" && (candidature.signRefuse && candidature.signRefuse === true)){
				return "bg-red"
			} else if (indexEtat === 1 || indexEtat === 3 || indexEtat === 5) {
				return "bg-red"
			} else if (etat === "EN_ATTENTE") {
				return "bg-orange"
			} else if (indexEtat >= 1) {
				return "bg-green"
			}
		} else if (step === 3) {
			if (etat === "POSTE_OBTENU") {
				return "bg-green"
			} else if (etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" && (candidature.signRefuse && candidature.signRefuse === true)){
				return "bg-red"
			} else if (indexEtat === 3 || indexEtat === 5) {
				return "bg-red"
			} else if (etat === "ENTREVUE_PROPOSE") {
				return "bg-orange-breathing"
			} else if (etat === "ENTREVUE_ACCEPTE_ETUDIANT") {
				return "bg-orange"
			} else if (indexEtat > 5) {
				return "bg-green"
			}
		} else if (step === 4) {
			// TODO: IMPLEMENTER LA VERIFICATION DE SIGNATURE
			if (etat === "POSTE_OBTENU") {
				return "bg-green"
			} else if (etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" && (candidature.signRefuse && candidature.signRefuse === true)){
				return "bg-red"
			} else if (etat === "CONTRAT_CREE_PAR_GESTIONNAIRE" && (candidature.needsAttention && candidature.needsAttention === true)) {
				return "bg-orange-breathing"
			} else if (indexEtat > 7) {
				return "bg-green"
			} else if (etat === "ACCEPTE_APRES_ENTREVUE" || etat === "CONTRAT_CREE_PAR_GESTIONNAIRE") {
				return "bg-orange"
			}
		} else if (step === 5) {
			if (etat === "POSTE_OBTENU") {
				return "bg-green"
			}
		}
	}
	
	return (
		<>
			<div className="candidature-progressbar-frame">
				<div className="candidature-progressbar-segment">
					<div className="candidature-progressbar-segment-info">
						<Icon path={mdiSendOutline} size={0.6}/>
						<p>{t('candidature_progress.send')}</p>
					</div>
					<div className={"candidature-progressbar-segment-bar " + (getEtatColor(1))}>
					
					</div>
				</div>
				<div className="candidature-progressbar-segment">
					<div className="candidature-progressbar-segment-info">
						<Icon path={mdiCheckCircleOutline} size={0.6}/>
						<p>{t('candidature_progress.selection')}</p>
					</div>
					<div className={"candidature-progressbar-segment-bar " + (getEtatColor(2))}>
					
					</div>
				</div>
				<div className="candidature-progressbar-segment">
					<div className="candidature-progressbar-segment-info">
						<Icon path={mdiForumOutline} size={0.6}/>
						<p>{t('candidature_progress.interview')}</p>
					</div>
					<div className={"candidature-progressbar-segment-bar " + (getEtatColor(3))}>
					
					</div>
				</div>
				<div className="candidature-progressbar-segment">
					<div className="candidature-progressbar-segment-info">
						<Icon path={mdiFileSign} size={0.6}/>
						<p>{t('candidature_progress.contract')}</p>
					</div>
					<div className={"candidature-progressbar-segment-bar " + (getEtatColor(4))}>
					
					</div>
				</div>
				<div className="candidature-progressbar-segment">
					<div className="candidature-progressbar-segment-info">
						<Icon path={mdiBriefcaseVariantOutline} size={0.6}/>
						<p>{t('candidature_progress.position')}</p>
					</div>
					<div className={"candidature-progressbar-segment-bar " + (getEtatColor(5))}>
					
					</div>
				</div>
			</div>
		</>
	)
}
export default CandidatureProgress