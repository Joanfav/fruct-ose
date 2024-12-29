import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../components/providers/AuthProvider";
import {useTranslation} from "react-i18next";

const PdfFromContratModal = ({ show, handleClose, contrat }) => {
    const {t} = useTranslation();
    const [pdf, setPdf] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { currentToken } = useContext(AuthContext);

    useEffect(() => {
        if (show && contrat) {
            downloadPdf();
        }
    }, [show, contrat, currentToken]);

    const downloadPdf = async () => {
        setLoading(true);
        try {
            const response = await fetch(`/contrats/${contrat.id}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": currentToken
                }
            });
            if (response.ok) {
                const blob = await response.blob();
                setPdf(URL.createObjectURL(blob));
            } else {
                setError("Une erreur s'est produite lors du téléchargement du PDF");
            }
        } catch (e) {
            setError("Une erreur s'est produite lors du téléchargement du PDF");
        } finally {
            setLoading(false);
        }
    };

    if (!show) {
        return null;
    }

    return (
        <div className="modal-overlay">
            <div className="modal-content" style={{ width: '80%', height: '90vh', maxWidth: '1200px', maxHeight: '90vh' }}>
                <h4>{t('modal.view_contrat')}</h4>
                {loading && <p>{t("modal.loading_pdf")}</p>}
                {error && <p style={{ color: 'red' }}>{error}</p>}
                {pdf && (
                    <iframe
                        src={pdf}
                        width="100%"
                        height="90%"
                        title="PDF Preview"
                        style={{ border: 'none', flexGrow: 1 }}
                    ></iframe>
                )}
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', margin: '10px 0' }}>
                    <button onClick={handleClose}>{t('modal.close')}</button>
                </div>
            </div>
        </div>
    );
};

export default PdfFromContratModal;