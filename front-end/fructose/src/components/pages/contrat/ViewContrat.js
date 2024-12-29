import React, {useContext, useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {ContratContext} from "../../providers/ContratProvider";
import PdfPreview from "../../../utilities/pdf/PdfPreview";
import {AuthContext} from "../../providers/AuthProvider";

const ViewContrat = ({contrat, handleSign, currentCandidature, handleNoSign}) => {
	const {t} = useTranslation();
	const {fetchPdfByContratId} = useContext(ContratContext);
	const [pdfUrl, setPdfUrl] = useState('');
	const {currentUser} = useContext(AuthContext);

	const isInitialized = useRef(false)

	useEffect(() => {
		let isActive = true;
		if (contrat && currentCandidature && contrat.candidatureId === currentCandidature.id && !isInitialized.current) {
			(async function () {
				try {
					const pdfUrl = await fetchPdfByContratId(contrat.id);
					if (isActive) setPdfUrl(pdfUrl);
				} catch (error) {
					console.error('Error fetching PDF:', error);
					if (isActive) setPdfUrl(null);
				}
			})();

			isInitialized.current = true
		} else {
			setPdfUrl(null);
		}

		/*return () => {
			isActive = false;
		};*/
	}, [contrat, currentCandidature, fetchPdfByContratId]);


	
	return (
		<>
			<div>
				<PdfPreview file={pdfUrl}/>
			</div>
			<div className="toolbar-items" style={{padding: "10px"}}>
				{contrat &&
				((["Non signe"].includes(contrat.signatureEmployeur) && currentUser.role === "EMPLOYEUR") ||
					(["Non signe"].includes(contrat.signatureEtudiant) && currentUser.role === "ETUDIANT")) &&
				!["Refuse"].includes(contrat.signatureEmployeur) &&
				!["Refuse"].includes(contrat.signatureEtudiant) ? (
					<>
						<button className="btn-outline toolbar-spacer" onClick={handleNoSign}>
							{t("dashboard_home_page.no_sign")}
						</button>
						<button className="btn-filled toolbar-spacer" onClick={handleSign}>
							{t("dashboard_home_page.sign")}
						</button>
					</>
				) : null}
			</div>
		</>
	)
}

export default ViewContrat;