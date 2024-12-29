import React, {useContext, useEffect, useState} from "react";
import Icon from "@mdi/react";
import {AuthContext} from "../../providers/AuthProvider";
import {Link, useLocation} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {OffreStageContext} from "../../providers/OffreStageProvider";
import OfferPreview from "./OfferPreview";
import {
    mdiAlertCircleOutline,
    mdiArrowLeft,
    mdiBookEducationOutline,
    mdiBriefcaseOutline,
    mdiBriefcaseRemove,
    mdiCashMultiple,
    mdiChevronUp,
    mdiDomain,
    mdiFilterMultipleOutline,
    mdiSchoolOutline
} from "@mdi/js";
import {ApplyOffreWindowContext} from "./ApplyOffreWindow";
import {CandidatureContext} from "../../providers/CandidatureProvider";

const DiscoverOffers = () => {

    const {t} = useTranslation();
    const {fetchOffresStage} = useContext(OffreStageContext);
    const {isUserInit, currentUser} = useContext(AuthContext);
    const {openCandidatureWindow} = useContext(ApplyOffreWindowContext);
    const {fetchCandidaturesById, candidatures} = useContext(CandidatureContext);
    const [offers, setOffers] = useState([])
    const [currentOffer, setCurrentOffer] = useState(null)
    const [displayFiltreWindow, setDisplayFiltreWindow] = useState(false)
    const [filterCount, setFilterCount] = useState(0)
    const [filteredOffers, setFilteredOffers] = useState(null)
    const location = useLocation();
    const offerId = new URLSearchParams(location.search).get("offer");

    const handleApplyStage = async () => {
        const isConfirmed = await openCandidatureWindow(currentOffer);
        if (isConfirmed) {
            const updatedOffers = offers.map(item =>
                item.id === currentOffer.id ? { ...item, hasCandidature: true } : item
            );
            const updatedFilteredOffers = filteredOffers.map(item =>
                item.id === currentOffer.id ? { ...item, hasCandidature: true } : item
            );
            setOffers(updatedOffers);
            setFilteredOffers(updatedFilteredOffers)
            setCurrentOffer({...currentOffer, hasCandidature: true})
        }
    };

    const createSessionList = () => {
        const sessions = [];
        for (const offer of offers) {
            const dateDebut = new Date(offer.dateDebut);
            if (dateDebut.getMonth() >= 0 && dateDebut.getMonth() <= 4) {
                const winterSession = `${t("discover_offers_page.filters.sessions.hiver")} ${dateDebut.getFullYear()}`;
                if (!sessions.includes(winterSession)) {
                    sessions.push(winterSession);
                }
            }
            if (dateDebut.getMonth() >= 8 && dateDebut.getMonth() <= 11) {
                const fallSession = `${t("discover_offers_page.filters.sessions.automne")} ${dateDebut.getFullYear()}`;
                if (!sessions.includes(fallSession)) {
                    sessions.push(fallSession);
                }
            }
        }
        return sessions;
    }

    const sessions = createSessionList();

    const [filters, setFilters] = useState(
        {
            type: {default: "tous", value: "tous"},
            emplacement: {default: "tous", value: "tous"},
            tauxHoraire: {default: 0, value: 0},
            departmenet: {default: 0, value: 0},
            sessions: {default: "tous", value: "tous"}
        }
    )

    const filterFields =
        [
            {
                name: t("discover_offers_page.filters.internship_type.title"),
                idName: "type",
                icon: mdiBriefcaseOutline,
                fields: [
                    {type: "radio", label: t("discover_offers_page.filters.all"), value: "tous"},
                    {
                        type: "radio",
                        label: t("discover_offers_page.filters.internship_type.temps_partiel"),
                        value: "temps_partiel"
                    },
                    {
                        type: "radio",
                        label: t("discover_offers_page.filters.internship_type.temps_plein"),
                        value: "temps_plein"
                    }
                ]
            },
            {
                name: t("discover_offers_page.filters.emplacement.title"),
                idName: "emplacement",
                icon: mdiDomain,
                fields: [
                    {type: "radio", label: t("discover_offers_page.filters.all"), value: "tous"},
                    {
                        type: "radio",
                        label: t("discover_offers_page.filters.emplacement.presentiel"),
                        value: "presentiel"
                    },
                    {
                        type: "radio",
                        label: t("discover_offers_page.filters.emplacement.virtuel"),
                        value: "virtuel"
                    },
                    {
                        type: "radio",
                        label: t("discover_offers_page.filters.emplacement.hybride"),
                        value: "hybride"
                    }
                ]
            },
            {
                name: t("discover_offers_page.filters.taux_horaire_minimum.title"),
                idName: "tauxHoraire",
                icon: mdiCashMultiple,
                fields: [
                    {type: "number", value: 0, min: 0, max: 50}
                ]
            },
            {
                name: t("discover_offers_page.filters.sessions.title"),
                idName: "sessions",
                icon: mdiSchoolOutline,
                fields: [
                    {type: "radio", label: t("discover_offers_page.filters.all"), value: "tous"},
                ]
            }
        ]

    const sortedSessions = sessions.sort((a, b) => {
        const yearA = parseInt(a.split(" ")[1]);
        const yearB = parseInt(b.split(" ")[1]);
        return yearA - yearB;
    });

    sortedSessions.forEach(session => {
        const translatedLabel = t(`${session}`) || session;
        filterFields.find(field => field.idName === "sessions").fields.push({
            type: "radio",
            label: translatedLabel,
            value: session
        });
    });

    useEffect(() => {
        if (currentUser) {
            fetchCandidaturesById(currentUser.id);
        }
        // Il dit d'inclure fetchCandidaturesById dans le tableau de dépendances, et il commence à spam des requetes dans Reseau
    }, [currentUser, currentOffer]);

    const isOfferApplied = (offer) => {
        return offer.hasCandidature || candidatures.some((candidature) => candidature.offreStageId === offer.id);
    };

    useEffect(() => {
        if (isUserInit) {
            (async function () {
                try {
                    const response = await fetchOffresStage();
                    setOffers(response);
                    setFilteredOffers(filterOffers(response, filters));
                } catch (error) {
                    console.error("Erreur lors de la récupération des offres :", error);
                }
            })();
        }
    }, [isUserInit, filters]);

    useEffect(() => {
        if(!currentOffer) {
            if (offers.length > 0) {
                if (offerId) {
                    const selectedOffer = offers.find((offer) => offer.id === parseInt(offerId));
                    setCurrentOffer(selectedOffer);
                } else {
                    setCurrentOffer(offers[0])
                }
            }
        }
    }, [location.search, offerId, offers]);

    const filterOffers = (offers, filters) => {
        let finalOffer = []
        for (let i = 0; i < offers.length; i++) {
            let currentOffer = offers[i]
            let isEligible = false
            let filterCount = 0
            if (filters.type) {
                isEligible = filters.type.value === "tous" || currentOffer.modaliteTravail === filters.type.value;
                if (filters.type.default !== filters.type.value) {
                    filterCount++
                }
            }
            if (filters.emplacement && isEligible === true) {
                isEligible = filters.emplacement.value === "tous" || currentOffer.typeEmploi === filters.emplacement.value;
                if (filters.emplacement.default !== filters.emplacement.value) {
                    filterCount++
                }
            }
            if (filters.tauxHoraire) {
                isEligible = (isEligible === true && currentOffer.tauxHoraire >= filters.tauxHoraire.value)
                if (filters.tauxHoraire.default !== parseInt(filters.tauxHoraire.value)) {
                    filterCount++
                }
            }
            if (filters.sessions && isEligible === true) {
                const dateDebut = new Date(currentOffer.dateDebut);
                const session = (dateDebut.getMonth() >= 0 && dateDebut.getMonth() <= 4) ? t("discover_offers_page.filters.sessions.hiver") + " " + dateDebut.getFullYear() : (dateDebut.getMonth() >= 5 && dateDebut.getMonth() <= 7) ? t("discover_offers_page.filters.sessions.automne") + " " + dateDebut.getFullYear() : (dateDebut.getMonth() >= 8 && dateDebut.getMonth() <= 11) ? "Automne " + dateDebut.getFullYear() : "";
                isEligible = ((filters.sessions.value === "tous" && (dateDebut.getMonth() <= 4 || (dateDebut.getMonth() >= 5 && dateDebut.getMonth() <= 7) || dateDebut.getMonth() >= 8)) || filters.sessions.value === session);
                if (filters.sessions.default !== filters.sessions.value) {
                    filterCount++;
                }
            }
            if (isEligible === true) {
                finalOffer.push(currentOffer)
            }
            setFilterCount(filterCount)
        }
        return finalOffer
    }

    const handleOfferFilterSelection = () => {
        let newOffers = filterOffers(offers, filters)
        setFilteredOffers(newOffers)
        setDisplayFiltreWindow(false)
        if (newOffers.length > 0) {
            setCurrentOffer(newOffers[0])
        } else {
            setCurrentOffer(null);
        }
    }

    const resetOfferFilterSelection = () => {
        // NE PAS ENLEVER KEY! SINON ÇA VA PAS COMPILE
        for (const [key, value] of Object.entries(filters)) {
            value.value = value.default
        }
        handleOfferFilterSelection()
    }

    const handleOfferSelection = (offer) => {
        setCurrentOffer(offer);
    };

    const handleFilterSelection = (field, value) => {
        let newFilter = filters;
        if (newFilter[field] !== null) {
            newFilter[field].value = value;
            setFilters(newFilter);
        }
    }

    const renderFields = () => {
        let finalForm = (<></>)
        for (let i = 0; i < filterFields.length; i++) {
            let currentField = filterFields[i]
            let formField = (<></>)
            for (let j = 0; j < currentField.fields.length; j++) {
                let currentElement = currentField.fields[j]
                switch (currentElement.type) {
                    case "radio":
                        formField = (
                            <>
                                {formField}
                                <input type="radio"
                                       defaultChecked={currentElement.value === filters[currentField.idName].value}
                                       name={currentField.idName} id={currentField.idName + "." + currentElement.value}
                                       onChange={() => {
                                           handleFilterSelection(currentField.idName, currentElement.value)
                                       }}/>
                                <label
                                    htmlFor={currentField.idName + "." + currentElement.value}>{currentElement.label}</label>
                                <br/>
                            </>
                        )
                        break
                    case "number":
                        formField = (
                            <>
                                {formField}
                                <input type="number" defaultValue={filters[currentField.idName].value}
                                       min={currentElement.min} max={currentElement.max} name={currentField.idName}
                                       id="" onChange={(e) => {
                                    handleFilterSelection(currentField.idName, e.target.value)
                                }}/>
                            </>
                        )
                        break
                    default:
                        formField = (
                            <>
                                {formField}
                                <input type="text" defaultValue={filters[currentField.idName].value}
                                       min={currentElement.min} max={currentElement.max} name={currentField.idName}
                                       id="" onChange={(e) => {
                                    handleFilterSelection(currentField.idName, e.target.value)
                                }}/>
                            </>
                        )
                        break
                }
            }
            formField = (
                <>
                    <div className="list-bullet">
                        <Icon path={currentField.icon} size={1}/>
                        <div style={{padding: "4px 0"}}>
                            <h6 className="m-0" style={{marginBottom: "5px"}}>{currentField.name}</h6>
                            {formField}
                        </div>
                    </div>
                </>
            )
            finalForm = (
                <>
                    {finalForm}
                    {formField}
                </>
            )
        }
        return finalForm
    }

    const getOfferListSection = () => {
        if (offers.length > 0) {
            return (
                <div style={{width: "45%"}}>
                    <div className={"dashboard-card"} style={{display: (displayFiltreWindow) ? "block" : "none"}}>
                        <section>
                            <div className="toolbar-items">
                                <h5 className="m-0">{t("discover_offers_page.filters.title")}</h5>
                                <div className="toolbar-spacer"></div>
                                <button className="btn-icon" onClick={() => {
                                    setDisplayFiltreWindow(false)
                                }}><Icon path={mdiChevronUp} size={1}/></button>
                            </div>
                            <br/>

                            <div>{renderFields()}</div>

                            <div className="list-bullet"
                                 style={{display: (currentUser.role === "EMPLOYEUR") ? "block" : "none"}}>
                                <Icon path={mdiBookEducationOutline} size={1}/>
                                <div style={{padding: "4px 0"}}>
                                    <h6 className="m-0"
                                        style={{marginBottom: "5px"}}>{t("discover_offers_page.filters.department")}</h6>
                                    <select name="" id="" disabled="disabled">
                                        <option value="">{t("programme." + currentUser.departementDTO.nom)}</option>
                                    </select>
                                </div>
                            </div>

                            <div className="toolbar-items">
                                <div className="toolbar-spacer"></div>
                                <button onClick={() => {
                                    resetOfferFilterSelection()
                                }}>{t("discover_offers_page.reset")}
                                </button>
                                <button className="btn-filled" onClick={() => {
                                    handleOfferFilterSelection()
                                }}>{t("discover_offers_page.search")}
                                </button>
                            </div>
                        </section>
                    </div>
                    <div className="dashboard-card" style={{minHeight: "480px"}}>
                        <section>
                            <div>
                                <div className="toolbar-items">
                                    <h5 className="m-0">{filteredOffers.length} {t("discover_offers_page.results")}</h5>
                                    <div className="toolbar-spacer"></div>
                                    <button onClick={() => {
                                        setDisplayFiltreWindow(!displayFiltreWindow)
                                    }}>
                                        <Icon path={mdiFilterMultipleOutline}
                                              size={1}/> {t("discover_offers_page.filters.title")} ({filterCount})
                                    </button>
                                </div>
                                <br/>
                                <div className="menu-list">
                                    {filteredOffers.reverse().map((item, index) => (
                                        <div onClick={() => handleOfferSelection(item)} key={index}
                                             className={`menu-list-item ${currentOffer && item.id === currentOffer.id ? "menu-list-item-selected" : ""}`}
                                             style={{
                                                 width: "100%",
                                                 padding: "0",
                                                 height: "170px",
                                                 boxSizing: "border-box"
                                             }}>
                                            <div style={{padding: "16px"}}>
                                                <p style={{
                                                    fontSize: "11px",
                                                    textTransform: "uppercase"
                                                }}
                                                   className="text-dark">
                                                    {t("programme." + item.departementDTO.nom)}
                                                </p>
                                                <h4 className="m-0">{item.poste}</h4>
                                                <p>{item.ownerDTO.companyName}</p>
                                                <p>{item.adresse}</p>
                                                {
                                                    (item.nombrePostes <= 5) ?
                                                        <span className="badge text-mini"><Icon
                                                            path={mdiAlertCircleOutline}
                                                            size={0.5}/>{t("discover_offers_page.limited_places")}</span>
                                                        : <></>
                                                }
                                            </div>
                                        </div>
                                    ))}
                                </div>
                                <br/>
                            </div>
                        </section>
                    </div>
                </div>
            );
        }
    };

    const getOffreListSection = () => {
        if (offers === null || filteredOffers === null) {
            return (
                <div className="dashboard-card" style={{width: "45%", height: "420px"}}>
                    <div className="loader-container">
                        <div className="loader">

                        </div>
                    </div>
                </div>
            );
        } else if (offers.length === 0) {
            return (
                <div className="dashboard-card" style={{width: "45%"}}>
                    <div className="dashboard-placeholder-card" style={{backgroundColor: "transparent"}}>
                        <div style={{textAlign: "center"}}>
                            <Icon path={mdiBriefcaseRemove} size={2}/>
                            <h6 style={{margin: "8px 0 14px 0"}}>{t("discover_offers_page.no_offers")}</h6>
                            <p className="text-dark">{t("discover_offers_page.future_offers")}</p>
                        </div>
                    </div>
                </div>
            );
        } else {
            return (
                <>
                    {getOfferListSection()}
                </>

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
                <h1>{t("discover_offers_page.title")}</h1>
            </div>
            <div style={{display: "flex", gap: "20px", alignItems: "start"}}>
                {getOffreListSection()}
                {currentOffer &&
                    <div className="dashboard-card" style={{
                        width: "55%",
                        position: "sticky",
                        top: "70px",
                        height: "90vh",
                    }}>
                        <OfferPreview
                            currentOffer={currentOffer}
                            handleApply={handleApplyStage}
                            isApplied={isOfferApplied(currentOffer)}
                        />
                    </div>
                }
            </div>
        </>
    );
};

export default DiscoverOffers;