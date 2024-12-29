import Icon from "@mdi/react";
import {mdiAccountCircle, mdiBriefcaseEyeOutline, mdiBriefcaseVariantOutline} from "@mdi/js";
import React from "react";
import {Link} from "react-router-dom";

const SuggestionsDashboard = ({role}) => {
    if (role === "EMPLOYEUR") {
        return (
            <>
                <section className="nospace">
                    <h5>Suggestions</h5>
                    <Link to="/dashboard/creer-offre-stage" className="text-decoration-none">
                        <div className="banner bg-primary">
                            <Icon path={mdiBriefcaseVariantOutline} size={1.2}/>
                            <div>
                                <h5 className="m-0">Créez une offre de stage</h5>
                                <p className="m-0">Ajoutez une offre de stage et commencez à recevoir des candidatures
                                    dès maintenant</p>
                            </div>
                        </div>
                    </Link>
                    <Link to="/dashboard/view-candidatures" className="text-decoration-none">
                        <div className="banner bg-grey">
                            <Icon path={mdiBriefcaseEyeOutline} size={1.2}/>
                            <div className="toolbar-spacer">
                                <h5 className="m-0">Surveillez vos candidatures</h5>
                                <p className="m-0">Effectuez les actions requises de votre part</p>
                            </div>
                        </div>
                    </Link>
                </section>
                <hr/>
            </>
        )
    } else if (role === "ADMIN") {

    } else if (role === "PROFESSEUR") {
        return (
            <>
                <section className="nospace">
                    <h5>Suggestions</h5>
                    <Link to="/dashboard" className="text-decoration-none">
                        <div className="banner bg-primary">
                            <Icon path={mdiBriefcaseVariantOutline} size={1.2}/>
                            <div>
                                <h5 className="m-0">Explorez les offres de stage</h5>
                                <p className="m-0">Explorez les offres qui se situent dans votre département</p>
                            </div>
                        </div>
                    </Link>
                </section>
                <hr/>
            </>
        )
    } else if (role === "ETUDIANT") {
        return (
            <>
                <section className="nospace">
                    <h5>Suggestions</h5>
                    <Link to="/dashboard/manage-cvs" className="text-decoration-none">
                        <div className="banner bg-primary">
                            <Icon path={mdiAccountCircle} size={1.2}/>
                            <div>
                                <h5 className="m-0">Complétez votre profil</h5>
                                <p className="m-0">Ajoutez un CV et commencez à postuler dès maintenant</p>
                            </div>
                        </div>
                    </Link>
                    <Link to="/dashboard/discover-offers" className="text-decoration-none">
                        <div className="banner bg-grey">
                            <Icon path={mdiBriefcaseVariantOutline} size={1.2}/>
                            <div className="toolbar-spacer">
                                <h5 className="m-0">Commencez à postuler</h5>
                                <p className="m-0">Plusieures offres de stage disponibles</p>
                            </div>
                        </div>
                    </Link>
                    <Link to="/dashboard#candidatures" className="text-decoration-none">
                        <div className="banner bg-grey">
                            <Icon path={mdiBriefcaseEyeOutline} size={1.2}/>
                            <div className="toolbar-spacer">
                                <h5 className="m-0">Surveillez vos candidatures</h5>
                                <p className="m-0">Effectuez les actions requises de votre part</p>
                            </div>
                        </div>
                    </Link>
                </section>
                <hr/>
            </>
        )
    }
}

export default SuggestionsDashboard