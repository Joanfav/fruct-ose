import React, {useContext, useEffect, useState} from "react";
import {ContratContext} from "../../providers/ContratProvider";
import Icon from "@mdi/react";
import {mdiBriefcasePlusOutline, mdiFileSign} from "@mdi/js";
import {t} from "i18next";
import PdfFromContratModal from "../../../utilities/modal/PdfFromContratModal";

const ViewContrats = ({needFetchingContracts, setNeedFetchingContracts}) => {
    const [contrats, setContrats] = useState([]);
    const {fetchContrats} = useContext(ContratContext);
    const [currentPage, setCurrentPage] = useState(1);
    const [showModal, setShowModal] = useState(false);
    const [selectedContrat, setSelectedContrat] = useState(null);
    const itemsPerPage = 10;

    useEffect(() => {
        console.log('Fetching contrats');
        fetchData();
        if (needFetchingContracts) {
            setNeedFetchingContracts(false);
        }
    }, [needFetchingContracts]);

    const fetchData = async () => {
        const response = await fetchContrats();
        if (response) {
            setContrats(response);
        }
    };

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const getSignatureStatus = (contrat) => {
        const etudiantSignature = contrat.signatureEtudiant;
        const gestionnaireSignature = contrat.signatureGestionnaire;
        const compagnieSignature = contrat.signatureEmployeur;

        if (etudiantSignature === "Refuse" || gestionnaireSignature === "Refuse" || compagnieSignature === "Refuse") {
            return {text: t("manage_contract_page.one_refused_contract_text"), color: "bg-red"};
        } else if (etudiantSignature === "Non signe" || gestionnaireSignature === "Non signe" || compagnieSignature === "Non signe") {
            return {text: t("manage_contract_page.waiting_sign_from_parties"), color: "bg-orange"};
        } else {
            return {text: t("manage_contract_page.contract_signed"), color: "bg-green"};
        }
    };

    const openModal = (contrat) => {
        setSelectedContrat(contrat);
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setSelectedContrat(null);
    };

    const startIndex = (currentPage - 1) * itemsPerPage;
    const selectedContrats = contrats.slice(startIndex, startIndex + itemsPerPage);
    const totalPages = Math.ceil(contrats.length / itemsPerPage);

    return (
        <div>
            <h4>{t("manage_contract_page.title_see_contract")}</h4>
            {contrats.length > 0 ? (
                <div className="menu-list">
                    {selectedContrats.map((contrat, index) => {
                        const status = getSignatureStatus(contrat);
                        return (
                            <div key={index} className="menu-list-item"
                                 onClick={() => openModal(contrat)}
                            >
                                <Icon path={mdiFileSign} size={1} />
                                <p className="m-0"
                                   style={{flexGrow: 1}}>{contrat.candidatureDTO.etudiantDTO.fullName} - {contrat.candidatureDTO.offreStageDTO.compagnie} - {contrat.candidatureDTO.offreStageDTO.nom}</p>
                                <small className={`badge text-mini ${status.color}`}>{status.text}</small>
                            </div>
                        );
                    })}
                </div>
            ) : (
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
            )}
            {contrats.length > itemsPerPage && (
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
                            }}
                        >
                            {index + 1}
                        </button>
                    ))}
                </div>
            )}
            {selectedContrat && (
                <PdfFromContratModal
                    show={showModal}
                    handleClose={closeModal}
                    contrat={selectedContrat}
                />
            )}
        </div>
    );
}

export default ViewContrats;