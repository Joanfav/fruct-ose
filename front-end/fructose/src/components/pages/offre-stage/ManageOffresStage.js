import React, {useContext, useEffect, useState} from "react";
import {mdiArrowLeft, mdiBriefcasePlusOutline, mdiCheck, mdiClockOutline, mdiClose} from "@mdi/js";
import Icon from "@mdi/react";
import {AuthContext} from "../../providers/AuthProvider";
import {Link} from "react-router-dom";
import {OffreStageContext} from "../../providers/OffreStageProvider";
import {useTranslation} from "react-i18next";

const ManageOffresStage = () => {
	const {t} = useTranslation();
	const {currentUser} = useContext(AuthContext);
	const {fetchOffresStage, fetchOffreStage} = useContext(OffreStageContext);
	const [offreStages, setOffreStages] = useState([]);
	const [setOffreStage] = useState([]);
	const [currentOffreStage, setCurrentOffreStage] = useState(null);
	
	useEffect(() => {
		if (currentUser) {
			(async function () {
				if (currentUser.role === "EMPLOYEUR") {
					try {
						const response = await fetchOffresStage();
						setOffreStages(response);
					} catch (error) {
						console.log("error" + error);
					}
				}
			})();
		}
	}, [currentUser, fetchOffresStage]);
	
	const getOffreStageById = async (id) => {
		try {
			const data = await fetchOffreStage(id);
			setOffreStage(data);
		} catch (error) {
			console.error("Error fetching offer:", error);
		}
	};
	
	const handleOffreStageSelection = (offreStage) => {
		if (currentOffreStage && offreStage.id === currentOffreStage.id) {
			setCurrentOffreStage(null);
		} else {
			setCurrentOffreStage(offreStage);
			getOffreStageById(offreStage.id);
		}
	};
	
	const getStatutElement = (offreStage) => {
		if (offreStage) {
			if (!offreStage.isApproved && !offreStage.isRefused) {
				return (
					<>
						<p className="m-0 text-orange">{t('manage_offre_stage.status.pending')}</p>
						<Icon path={mdiClockOutline} size={0.8} className="text-orange"/>
					</>
				);
			} else if (offreStage.isApproved) {
				return (
					<>
						<p className="m-0 text-green">{t('manage_offre_stage.status.approved')}</p>
						<Icon path={mdiCheck} size={0.8} className="text-green"/>
					</>
				);
			} else if (offreStage.isRefused) {
				return (
					<>
						<p className="m-0 text-red">{t('manage_offre_stage.status.rejected')}</p>
						<Icon path={mdiClose} size={0.8} className="text-red"/>
					</>
				);
			}
		}
		return null;
	};
	
	const getOffreStageList = () => {
		if (offreStages.length > 0) {
			return (
				<>
					<div className="toolbar-items">
						<div className="m-0 toolbar-spacer"></div>
						<Link to="../creer-offre-stage">
							<button className="btn-filled">{t('manage_offre_stage.buttons.add')}
								<Icon path={mdiBriefcasePlusOutline} size={1}/>
							</button>
						</Link>
					</div>
					<br/>
					<div className="menu-list">
						{offreStages.map((offreStage, index) => (
							<div key={index} onClick={() => handleOffreStageSelection(offreStage)}
							     className={`menu-list-item ${currentOffreStage && offreStage.id === currentOffreStage.id ? "menu-list-item-selected" : ""}`}>
								<Icon path={mdiBriefcasePlusOutline} size={1}/>
								<div>
									<p className="m-0">{offreStage.nom}</p>
									<div className="toolbar-items">
										{getStatutElement(offreStage)}
									</div>
								</div>
							</div>
						))}
					</div>
				</>
			);
		}
	};
	
	const getOffreStageListSection = () => {
		if (offreStages.length === 0) {
			return (
				<div className="dashboard-card" style={{width: "65%"}}>
					<div className="dashboard-placeholder-card" style={{backgroundColor: "transparent"}}>
						<div style={{textAlign: "center"}}>
							<Icon path={mdiBriefcasePlusOutline} size={2}/>
							<h6 style={{margin: "8px 0 14px 0"}}>{t('manage_offre_stage.messages.no_offre_stages')}</h6>
							<Link to="../creer-offre-stage">
								<button className="btn-filled">{t('manage_offre_stage.buttons.add')}
									<Icon path={mdiBriefcasePlusOutline} size={1}/>
								</button>
							</Link>
						</div>
					</div>
				</div>
			);
		} else {
			return (
				<div className="dashboard-card" style={{width: "65%"}}>
					<section>
						{getOffreStageList()}
					</section>
				</div>
			);
		}
	};
	
	return (
		<>
			<div className="dashboard-card-toolbar">
				<Link to="/dashboard">
					<button className="btn-icon-dashboard">
						<Icon path={mdiArrowLeft} size={1.4}/>
					</button>
				</Link>
				<h1>{t('manage_offre_stage.titles.offre_stages')}</h1>
			</div>
			<div style={{display: "flex", gap: "20px", alignItems: "start"}}>
				{getOffreStageListSection()}
			</div>
		</>
	);
};

export default ManageOffresStage;