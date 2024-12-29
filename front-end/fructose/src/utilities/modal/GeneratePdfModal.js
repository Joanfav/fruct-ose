import React, {useContext, useEffect, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {ContratContext} from "../../components/providers/ContratProvider";

const GeneratePdfModal = ({onClose, onSave, candidatureId}) => {
	const {t} = useTranslation();
	const [pdfUrl, setPdfUrl] = useState('');
	const {fetchPdf} = useContext(ContratContext);
	
	useEffect(() => {
		const getPdf = async () => {
			try {
				const pdfUrl = await fetchPdf(candidatureId);
				setPdfUrl(pdfUrl);
			} catch (error) {
				console.error('Error fetching PDF:', error);
			}
		};
		getPdf();
	}, [candidatureId]);
	
	const handleSave = () => {
		onSave(candidatureId);
		onClose();
	};
	
	return (
		<div className="modal-overlay">
			<div
				className="modal-content"
				style={{
					width: '80%',
					height: '90vh',
					maxWidth: '1200px',
					maxHeight: '90vh',
				}}
			>
				<h4>{t('modal.generate_pdf')}</h4>
				{pdfUrl && (
					<iframe
						src={pdfUrl}
						width="100%"
						height="90%"
						title="PDF Preview"
						style={{
							border: 'none',
							flexGrow: 1,
						}}
					></iframe>
				)}
				<div
					style={{
						display: 'flex',
						justifyContent: 'flex-end',
						gap: '10px',
						margin: '10px 0'
					}}
				>
					<button onClick={onClose}>{t('modal.close')}</button>
					<button onClick={handleSave} className="btn-filled">{t('modal.generate')}</button>
				</div>
			</div>
		</div>
	);
};

export default GeneratePdfModal;