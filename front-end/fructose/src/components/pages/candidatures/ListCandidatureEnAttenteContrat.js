import React, {useContext, useEffect, useState} from 'react';
import {CandidatureContext} from '../../providers/CandidatureProvider';
import {AuthContext} from '../../providers/AuthProvider';
import GeneratePdfModal from '../../../utilities/modal/GeneratePdfModal';
import {mdiBriefcasePlusOutline, mdiFileSign} from "@mdi/js";
import Icon from "@mdi/react";
import {t} from "i18next";

const ListCandidatureEnAttenteContrat = ({needFetchingContracts}) => {
		const {fetchCandidatureByEtatAccepteApresEntrevue} = useContext(CandidatureContext);
		const {currentToken} = useContext(AuthContext);
		const [filteredCandidatures, setFilteredCandidatures] = useState([]);
		const [isModalOpen, setModalOpen] = useState(false);
		const [selectedCandidature, setSelectedCandidature] = useState(null);
		const [currentPage, setCurrentPage] = useState(1);
		const itemsPerPage = 10;
		
		useEffect(() => {
			const fetchData = async () => {
				const response = await fetchCandidatureByEtatAccepteApresEntrevue();
				if (response === undefined) {
					return;
				}
				setFilteredCandidatures(response);
			};
			fetchData();
		}, [selectedCandidature]);
		
		const handleClick = (candidature) => {
			setSelectedCandidature(candidature);
			setModalOpen(true);
		};
		
		const handleSaveContrat = async () => {
			const response = await fetch(`/contrats`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Authorization': currentToken
					},
					body: JSON.stringify(selectedCandidature)
				}
			);
			if (response.ok) {
				console.log('Contrat created successfully');
				await fetch(`/candidatures/modifierEtatCandidature/${selectedCandidature.id}?nouvelEtat=CONTRAT_CREE_PAR_GESTIONNAIRE`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Authorization': currentToken
					}
				});
				needFetchingContracts(true);
				setFilteredCandidatures(filteredCandidatures.filter(c => c.id !== selectedCandidature.id));
			} else {
				console.error('Error creating contrat');
			}
			setModalOpen(false);
			setSelectedCandidature(null);
		};
		
		const handlePageChange = (pageNumber) => {
			setCurrentPage(pageNumber);
		};
		
		const startIndex = (currentPage - 1) * itemsPerPage;
		const selectedCandidatures = filteredCandidatures.slice(startIndex, startIndex + itemsPerPage);
		const totalPages = Math.ceil(filteredCandidatures.length / itemsPerPage);
		
		return (
			<section style={{padding: 0}}>
				<div className="toolbar-items">
					<h4 className={"m-0 toolbar-spacer"}>{t("manage_contract_page.title_generate_contract")}</h4>
				</div>
				<div style={{padding: "10px 0"}}>
					{filteredCandidatures.length === 0 ? (
						<div style={{
							width: "400px",
							display: "flex",
							alignItems: "center",
							backgroundColor: "#eee",
							borderRadius: "5px",
							gap: "5px",
							padding: "10px"
						}}>
							<Icon path={mdiBriefcasePlusOutline} size={1}/>
							<p className="m-0">{t("manage_offre_stage.messages.no_offre_stages_contracts")}</p>
						</div>
					) : (

								<div className="menu-list">
									{selectedCandidatures.map((candidature, index) => (
										<div key={index} onClick={() => handleClick(candidature)}
										     className="menu-list-item">
											<Icon path={mdiFileSign} size={1} />
											<p className="m-0">{candidature.etudiantDTO.fullName} - {candidature.offreStageDTO.compagnie} - {candidature.offreStageDTO.nom}</p>
										</div>
									))}
								</div>
					)}
					{filteredCandidatures.length > itemsPerPage && (
						<div style={{
							display: "flex",
							justifyContent: "center",
							gap: "5px",
							marginTop: "20px"
						}}>
							{Array.from({length: totalPages}, (_, index) => (
								<button
									key={index}
									className={(currentPage === index + 1) ? "btn-filled" : ""}
									onClick={() => {
										handlePageChange(index + 1);
										setSelectedCandidature(null);
									}}
								>
									{index + 1}
								</button>
							))}
						</div>
					)}
					{isModalOpen && (
						<GeneratePdfModal
							onClose={() => {
								setModalOpen(false);
								setSelectedCandidature(null);
							}}
							onSave={handleSaveContrat}
							candidatureId={selectedCandidature.id}
						/>
					)}
				</div>
			</section>
		);
	}
;

export default ListCandidatureEnAttenteContrat;