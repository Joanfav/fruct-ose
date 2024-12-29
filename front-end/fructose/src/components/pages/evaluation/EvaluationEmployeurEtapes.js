import React, {useState, useContext, useRef} from "react";
import {useTranslation} from "react-i18next";
import {Link, useLocation} from "react-router-dom";
import {AuthContext} from "../../providers/AuthProvider";
import PdfPreview from "../../../utilities/pdf/PdfPreview";
import Icon from "@mdi/react";
import {mdiChevronLeft} from "@mdi/js";

const EvaluationEmployeurEtapes = () => {
    const {t} = useTranslation();
    const [currentStep, setCurrentStep] = useState(1); // Étape actuelle
    const {currentToken} = useContext(AuthContext);
    const location = useLocation();
    const initialCandidature = location.state?.candidature || {};
    const [errors, setErrors] = useState({});
    const [pdfPreviewUrl, setPdfPreviewUrl] = useState(null);
    const [isPdfPreviewVisible, setIsPdfPreviewVisible] = useState(false);

    const isSent = useRef(false)

    // Structure des données du formulaire
    const [formData, setFormData] = useState({
        candidature: initialCandidature.candidature || "",
        productivite: {commentaire: "", champs: {}},
        qualiteTravail: {commentaire: "", champs: {}},
        relationsInterpersonnelles: {commentaire: "", champs: {}},
        habiletesPersonnelles: {commentaire: "", champs: {}},
        appreciationGlobale: {commentaire: "", valeur: ""},
        evaluationDiscutee: "",
        nombreHeures: "",
        prochainStage: "",
        commentairesAccomplissementMandat: "",
        signature: "",
    });
    const ReponseEvaluation = {
        TOTAL_AGREE: t("evaluation_employeur.step.total_agree"),
        MOSTLY_AGREE: t("evaluation_employeur.step.mostly_agree"),
        MOSTLY_DISAGREE: t("evaluation_employeur.step.mostly_disagree"),
        TOTAL_DISAGREE: t("evaluation_employeur.step.total_disagree"),
    };

    // Gestion des changements dans les champs
    const handleChange = (e) => {
        const {name, value} = e.target;
        const keys = name.split(".");

        setFormData((prev) => {
            const updatedFormData = {...prev};

            // Gestion des champs imbriqués
            if (keys.length === 2) {
                const [section, key] = keys;
                updatedFormData[section][key] = value;
            } else if (keys.length === 3) {
                const [section, type, key] = keys;
                if (type === "champs") {
                    updatedFormData[section].champs[key] = value;
                }
            } else {
                updatedFormData[name] = value;
            }
            console.log(updatedFormData);
            return updatedFormData;
        });
    };

    const handleNextStep = () => {
        const stepFields = {
            1: {
                section: "productivite",
                fields: [
                    t("evaluation_employeur.step.productivite.fields.field1"),
                    t("evaluation_employeur.step.productivite.fields.field2"),
                    t("evaluation_employeur.step.productivite.fields.field3"),
                    t("evaluation_employeur.step.productivite.fields.field4"),
                    t("evaluation_employeur.step.productivite.fields.field5"),
                ],
            },
            2: {
                section: "qualiteTravail",
                fields: [
                    t("evaluation_employeur.step.qualiteTravail.fields.field1"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field2"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field3"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field4"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field5"),
                ],
            },
            3: {
                section: "relationsInterpersonnelles",
                fields: [
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field1"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field2"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field3"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field4"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field5"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field6"),
                ],
            },
            4: {
                section: "habiletesPersonnelles",
                fields: [
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field1"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field2"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field3"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field4"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field5"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field6"),
                ],
            },
        };

        // Étapes 1 à 4 : Validation des champs
        if (currentStep <= 4) {
            const {section, fields} = stepFields[currentStep] || {};

            if (!fields) {
                console.error("Aucun champ trouvé pour l'étape actuelle :", currentStep);
                return;
            }

            const newErrors = {};

            fields.forEach((field) => {
                if (!formData[section]?.champs?.[field]?.trim()) {
                    newErrors[`${section}.champs.${field}`] = "Ce champ est obligatoire.";
                }
            });

            if (Object.keys(newErrors).length > 0) {
                setErrors(newErrors); // Mettre à jour les erreurs
                return;
            }

            setErrors({});
        }

        if (currentStep === 5) {
            const appreciationFields = [
                "valeur",
                "commentaire",
            ];

            const newErrors = {};

            appreciationFields.forEach((field) => {
                if (!formData.appreciationGlobale[field]?.trim()) {
                    newErrors[`appreciationGlobale.${field}`] = "Ce champ est obligatoire.";
                }
            });

            if (Object.keys(newErrors).length > 0) {
                setErrors(newErrors);
                return;
            }

            setErrors({});
        }

        if (currentStep === 6) {
            const finalFields = [
                "evaluationDiscutee",
                "nombreHeures",
                "prochainStage",
            ];

            const newErrors = {};

            finalFields.forEach((field) => {
                if (!formData[field]?.trim()) {
                    newErrors[field] = "Ce champ est obligatoire.";
                }
            });

            if (Object.keys(newErrors).length > 0) {
                setErrors(newErrors);
                return;
            }

            setErrors({});
        }

        if (currentStep < 6) {
            setCurrentStep((prev) => prev + 1);
        }
    };


    const handlePreviousStep = () => {
        if (currentStep > 1) setCurrentStep((prev) => prev - 1);
    };

    const getStepClass = (step) => {
        if (currentStep === step) {
            return "active"
        } else if (currentStep > step) {
            return "completed"
        }
        return ""
    }

    const handleSubmit = async () => {
        const finalFields = [
            "evaluationDiscutee",
            "nombreHeures",
            "prochainStage",
            "signature",
        ];

        const newErrors = {};
        finalFields.forEach((field) => {
            if (!formData[field]?.trim()) {
                newErrors[field] = "Ce champ est obligatoire.";
            }
        });

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        const preparedData = {
            candidatureDTO: {
                ...formData.candidature,
            },
            appreciationGlobale: formData.appreciationGlobale.valeur,
            commentaireAppreciationGlobale: formData.appreciationGlobale.commentaire,
            discuterAvecEtudiant: formData.evaluationDiscutee,
            nombreHeureEncadrement: parseFloat(formData.nombreHeures),
            acceuilleEleveProchainStage: formData.prochainStage,
            commentairesAccomplissementMandat: formData.commentairesAccomplissementMandat,
            signature:formData.signature,
            sections: Object.keys(formData)
                .filter((key) => ["productivite", "qualiteTravail", "relationsInterpersonnelles", "habiletesPersonnelles"].includes(key))
                .map((sectionKey) => ({
                    name: sectionKey,
                    commentaire: formData[sectionKey].commentaire,
                    criteres: Object.keys(formData[sectionKey].champs).map((question) => ({
                        question: question,
                        reponse: formData[sectionKey].champs[question],
                    })),
                })),
        };

        if(isSent.current === true){
            return
        }
        isSent.current = true

        try {
            const response = await fetch("/evaluations/employeur/creer", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": currentToken,
                },
                body: JSON.stringify(preparedData),
            });

            if (response.ok) {
                const pdfBytes = await response.arrayBuffer();
                const pdfBlob = new Blob([pdfBytes], { type: "application/pdf" });
                const pdfUrl = URL.createObjectURL(pdfBlob);

                setPdfPreviewUrl(pdfUrl);
                setIsPdfPreviewVisible(true);
            } else {
                console.error("Erreur lors de la soumission de l'évaluation");
            }
        } catch (error) {
            console.error("Erreur lors de la soumission de l'évaluation :", error);
        }
    };



    // Rendu des étapes
    const renderStep = () => {
        const stepOptions = [
            {
                title: t("evaluation_employeur.step.productivite.title"),
                description: t("evaluation_employeur.step.productivite.description"),
                items: [
                    t("evaluation_employeur.step.productivite.fields.field1"),
                    t("evaluation_employeur.step.productivite.fields.field2"),
                    t("evaluation_employeur.step.productivite.fields.field3"),
                    t("evaluation_employeur.step.productivite.fields.field4"),
                    t("evaluation_employeur.step.productivite.fields.field5"),
                ],
                section: "productivite"
            },
            {
                title: t("evaluation_employeur.step.qualiteTravail.title"),
                description: t("evaluation_employeur.step.qualiteTravail.description"),
                items: [
                    t("evaluation_employeur.step.qualiteTravail.fields.field1"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field2"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field3"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field4"),
                    t("evaluation_employeur.step.qualiteTravail.fields.field5"),
                ],
                section: "qualiteTravail"
            },
            {
                title: t("evaluation_employeur.step.relationsInterpersonnelles.title"),
                description:t("evaluation_employeur.step.relationsInterpersonnelles.description"),
                items: [
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field1"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field2"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field3"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field4"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field5"),
                    t("evaluation_employeur.step.relationsInterpersonnelles.fields.field6"),
                ],
                section: "relationsInterpersonnelles"
            },
            {
                title: t("evaluation_employeur.step.habiletesPersonnelles.title"),
                description: t("evaluation_employeur.step.habiletesPersonnelles.description"),
                items: [
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field1"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field2"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field3"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field4"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field5"),
                    t("evaluation_employeur.step.habiletesPersonnelles.fields.field6"),
                ],
                section: "habiletesPersonnelles"
            }
        ];

        if (currentStep <= stepOptions.length) {
            const {title, description, items, section} = stepOptions[currentStep - 1];
            return (
                <div className="stepper-form-step">
                    <h4>{title}</h4>
                    <p>{description}</p>
                    <br/>
                    {items.map((item, idx) => (
                        <div key={idx} style={{marginBottom: "15px"}}>
                                <div className="input-container">
                                    <p>
                                        {item}
                                    </p>
                                    <select
                                        id={`${section}.champs.${item}`}
                                        name={`${section}.champs.${item}`}
                                        value={formData[section]?.champs?.[item] || ""}
                                        onChange={handleChange}
                                        style={{
                                            border: errors[`${section}.champs.${item}`] ? "1px solid red" : "1px solid #ccc",
                                        }}
                                    >
                                        <option value="">{t("evaluation_employeur.step.select")}</option>
                                        {Object.entries(ReponseEvaluation).map(([key, label]) => (
                                            <option key={key} value={key}>
                                                {label}
                                            </option>
                                        ))}
                                    </select>
                                    {errors[`${section}.champs.${item}`] && (
                                        <p className="text-red m-0 text-mini">{errors[`${section}.champs.${item}`]}</p>
                                    )}
                            </div>
                        </div>
                    ))}
                    <div className="input-container">
                        <p>{t("evaluation_employeur.step.comment")}</p>
                        <textarea
                            name={`${section}.commentaire`}
                            value={formData[section].commentaire || ""}
                            onChange={handleChange}
                        ></textarea>
                    </div>
                </div>
            );
        } else if (currentStep === 5) {
            return (
                <div className="stepper-form-step">
                    <h4>{t("evaluation_employeur.step.appreciationGlobale.title")}</h4>
                    <br/>
                    <h6>{t("evaluation_employeur.step.appreciationGlobale.description")}</h6>
                    {[
                        t("evaluation_employeur.step.appreciationGlobale.options.option1"),
                        t("evaluation_employeur.step.appreciationGlobale.options.option2"),
                        t("evaluation_employeur.step.appreciationGlobale.options.option3"),
                        t("evaluation_employeur.step.appreciationGlobale.options.option4"),
                        t("evaluation_employeur.step.appreciationGlobale.options.option5"),
                    ].map((option, idx) => (
                        <div key={idx}>
                            <input
                                type="radio"
                                name="appreciationGlobale.valeur"
                                value={option}
                                onChange={handleChange}
                                checked={formData.appreciationGlobale.valeur === option}
                            />
                            <label>{option}</label>
                        </div>
                    ))}
                    {errors["appreciationGlobale.valeur"] && (
                        <p className="text-red">{errors["appreciationGlobale.valeur"]}</p>
                    )}
                    <br/>
                    <div className="input-container">
                        <p>{t("evaluation_employeur.step.appreciationGlobale.comment_label")}</p>
                        <textarea
                            name="appreciationGlobale.commentaire"
                            value={formData.appreciationGlobale.commentaire || ""}
                            onChange={handleChange}
                        ></textarea>
                    </div>
                    {errors["appreciationGlobale.commentaire"] && (
                        <p className="text-red">{errors["appreciationGlobale.commentaire"]}</p>
                    )}
                </div>
            );
        } else if (currentStep === 6) {
            return (
                <div className="stepper-form-step">
                    <h5>{t("evaluation_employeur.step.evaluationFinale.title")}</h5>
                    <br/>
                    <p className="m-0">{t("evaluation_employeur.step.evaluationFinale.discussion_label")}</p>
                    <div className="input-container">
                        <input
                            type="radio"
                            name="evaluationDiscutee"
                            value="Oui"
                            onChange={handleChange}
                            checked={formData.evaluationDiscutee === "Oui"}
                        />
                        <label>{t("evaluation_employeur.step.evaluationFinale.stage_options.yes")}</label>
                        <br/>
                        <input
                            type="radio"
                            name="evaluationDiscutee"
                            value="Non"
                            onChange={handleChange}
                            checked={formData.evaluationDiscutee === "Non"}
                        />
                        <label>{t("evaluation_employeur.step.evaluationFinale.stage_options.no")}</label>
                    </div>
                    {errors["evaluationDiscutee"] && (
                        <p className="text-red">{errors["evaluationDiscutee"]}</p>
                    )}
                    <div className="input-container">
                        <p>{t("evaluation_employeur.step.evaluationFinale.hours_label")}</p>
                        <input
                            type="number"
                            name="nombreHeures"
                            value={formData.nombreHeures || ""}
                            onChange={handleChange}
                        />
                    </div>
                    {errors["nombreHeures"] && (
                        <p className="text-red">{errors["nombreHeures"]}</p>
                    )}
                    <br/>
                    <p className="m-0">{t("evaluation_employeur.step.evaluationFinale.stage_label")}</p>
                    <div className="input-container">
                        <input
                            type="radio"
                            name="prochainStage"
                            value="Oui"
                            onChange={handleChange}
                            checked={formData.prochainStage === "Oui"}
                        />
                        <label>{t("evaluation_employeur.step.evaluationFinale.stage_options.yes")}</label>
                        <br/>
                        <input
                            type="radio"
                            name="prochainStage"
                            value="Non"
                            onChange={handleChange}
                            checked={formData.prochainStage === "Non"}
                        />
                        <label>{t("evaluation_employeur.step.evaluationFinale.stage_options.no")}</label>
                        <br/>
                        <input
                            type="radio"
                            name="prochainStage"
                            value="Peut-être"
                            onChange={handleChange}
                            checked={formData.prochainStage === "Peut-être"}
                        />
                        <label>{t("evaluation_employeur.step.evaluationFinale.stage_options.maybe")}</label>
                    </div>
                    {errors["prochainStage"] && (
                        <p className="text-red">{errors["prochainStage"]}</p>
                    )}
                    <br/>
                    <p>{t("evaluation_employeur.step.evaluationFinale.signature_label")}</p>
                    <div className="input-container">
                        <input
                            type="text"
                            name="signature"
                            value={formData.signature || ""}
                            onChange={handleChange}
                        />
                        {errors["signature"] && (
                            <p className="text-red">{errors["signature"]}</p>
                        )}
                    </div>
                </div>
            );
        } else {
            return <p>Étape invalide</p>;
        }
    };

    return (

        <div>
            <div className="form-full-body">
                <div className={"signup-frame"}>
                    <div className={"signup-head " + (currentStep !== 0 ? "signup-head-instep" : "") + " bg-secondary"}>
                        <h4>{t("evaluation_employeur.evaluation_steps.title")}</h4>
                        <br/>
                        {currentStep !== 0 ? (
                            <div className="vertical-stepper">
                                <div className={"vertical-stepper-item " + getStepClass(1)}>
                                    <div className="vertical-stepper-content">
                                        <h6 className="vertical-stepper-title">
                                            {t("evaluation_employeur.evaluation_steps.step1.title")}
                                        </h6>
                                        <p style={{fontStyle: "italic", margin: 0}}>
                                            {t("evaluation_employeur.evaluation_steps.step1.description")}
                                        </p>
                                    </div>
                                </div>
                                <div className={"vertical-stepper-item " + getStepClass(2)}>
                                    <div className="vertical-stepper-content">
                                        <h6 className="vertical-stepper-title">
                                            {t("evaluation_employeur.evaluation_steps.step2.title")}
                                        </h6>
                                        <p style={{fontStyle: "italic", margin: 0}}>
                                            {t("evaluation_employeur.evaluation_steps.step2.description")}
                                        </p>
                                    </div>
                                </div>
                                <div className={"vertical-stepper-item " + getStepClass(3)}>
                                    <div className="vertical-stepper-content">
                                        <h6 className="vertical-stepper-title">
                                            {t("evaluation_employeur.evaluation_steps.step3.title")}
                                        </h6>
                                        <p style={{fontStyle: "italic", margin: 0}}>
                                            {t("evaluation_employeur.evaluation_steps.step3.description")}
                                        </p>
                                    </div>
                                </div>
                                <div className={"vertical-stepper-item " + getStepClass(4)}>
                                    <div className="vertical-stepper-content">
                                        <h6 className="vertical-stepper-title">
                                            {t("evaluation_employeur.evaluation_steps.step4.title")}
                                        </h6>
                                        <p style={{fontStyle: "italic", margin: 0}}>
                                            {t("evaluation_employeur.evaluation_steps.step4.description")}
                                        </p>
                                    </div>
                                </div>
                                <div className={"vertical-stepper-item " + getStepClass(5)}>
                                    <div className="vertical-stepper-content">
                                        <h6 className="vertical-stepper-title">
                                            {t("evaluation_employeur.evaluation_steps.step5.title")}
                                        </h6>
                                    </div>
                                </div>
                                <div className={"vertical-stepper-item " + getStepClass(6)}>
                                    <div className="vertical-stepper-content">
                                        <h6 className="vertical-stepper-title">
                                            {t("evaluation_employeur.evaluation_steps.step6.title")}
                                        </h6>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div>
                                <h1>{t("evaluation_employeur.evaluation_steps.no_step")}</h1>
                            </div>
                        )}
                        <br/>
                        <br/>
                    </div>
                    <div className="signup-content p-0">
                        {!isPdfPreviewVisible ? (
                            <>
                                <div className="scrollable-content">
                                    {renderStep()}
                                </div>
                                <div className="form-dock">
                                    {currentStep > 1 && (
                                        <button
                                            onClick={handlePreviousStep}
                                        >
                                            <Icon path={mdiChevronLeft} size={1} />
                                        </button>
                                    )}
                                    <div className="toolbar-spacer"></div>
                                    {currentStep < 6 && (
                                        <button
                                            onClick={handleNextStep}
                                            className="btn-filled"
                                        >

                                            {t("evaluation_employeur.step.next")}
                                        </button>
                                    )}
                                    {currentStep === 6 && (
                                        <div>
                                            <button
                                                onClick={() => {
                                                    handleSubmit();
                                                }}
                                                className="btn-filled"
                                            >

                                                {t("evaluation_employeur.step.submit")}
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </>
                        ) : (
                            <div>
                                <div style={{padding: "10px"}}>
                                    <PdfPreview file={pdfPreviewUrl} height={500} filename="evaluation_stagiaire.pdf"/>
                                </div>
                                <Link to="/dashboard">
                                    <button
                                        className="btn-filled"
                                        style={{
                                            marginTop: "20px",
                                            display: "block",
                                            marginLeft: "auto",
                                            marginRight: "auto",
                                        }}
                                    >
                                        {t("evaluation_employeur.step.back_to_dashboard")}
                                    </button>
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EvaluationEmployeurEtapes;
