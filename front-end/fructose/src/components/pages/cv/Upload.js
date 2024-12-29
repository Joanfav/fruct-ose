import React, {useCallback, useContext, useEffect, useState} from "react";
import {
	mdiArrowLeft,
	mdiChevronRight,
	mdiClose,
	mdiCloudUploadOutline,
	mdiFileOutline,
	mdiFolderOpenOutline
} from "@mdi/js";
import Icon from "@mdi/react";
import {Link, useNavigate} from "react-router-dom";
import {useDropzone} from "react-dropzone";
import {useTranslation} from "react-i18next";
import PdfPreview from "../../../utilities/pdf/PdfPreview";
import {CvContext} from "../../providers/CvProvider";

const UploadCV = () => {
	
	const {t} = useTranslation();
	const {UploadCv} = useContext(CvContext);
	const [error, setError] = useState(null);
	const [file, setFile] = useState(null);
	const [filename, setFilename] = useState('');
	const navigate = useNavigate();
	const MAX_FILE_SIZE = 1048576;
	
	const onDrop = useCallback((acceptedFiles) => {
		const selectedFile = acceptedFiles[0];
		
		if (selectedFile.size > MAX_FILE_SIZE) {
			setError(t('upload_cv.error_file_size_exceeded', {maxSize: MAX_FILE_SIZE / 1024})); // Affichage en Ko
			return;
		}
		
		setFile(selectedFile);
		setFilename(selectedFile.name);
		setError(null);
	}, [t]);
	
	const {getRootProps, getInputProps, isDragActive, open} = useDropzone({
		onDrop,
		accept: {'application/pdf': ['.pdf']},
		noClick: true,
		multiple: false
	});
	
	const getPdfPreview = (file) => (
		<PdfPreview file={URL.createObjectURL(file)} height={300}></PdfPreview>
	);
	
	const handleSubmit = () => {
		setError(null);
		
		if (file) {
			UploadCv(file)
				.then((response) => {
					if (response.ok) {
						navigate("/dashboard/manage-cvs");
					} else {
						return response.text().then((text) => {
							setError(text || t('upload_cv.upload_error'));
						});
					}
				})
				.catch((error) => {
					setError(`${t('upload_cv.upload_error')}: ${error.message}`);
				});
		} else {
			setError(t('upload_cv.error_file_required'));
		}
	};
	
	const getStep = () => {
		if (file == null) {
			return (
				<>
					<div className="file-upload-zone" {...getRootProps()} style={{
						opacity: isDragActive ? "0.5" : "1",
						border: isDragActive ? "1px solid black" : "none"
					}}>
						<input {...getInputProps()} />
						<div className="file-upload-content">
							<Icon path={mdiCloudUploadOutline} size={2}/>
							<h5>{t('upload_cv.drag_drop')}</h5>
							{!isDragActive && (
								<>
									<div className="file-upload-or">
										<hr/>
										<p>{t('upload_cv.or')}</p>
										<hr/>
									</div>
									<button onClick={open} className="btn-outline">
										<Icon path={mdiFolderOpenOutline} size={0.8}/> {t('upload_cv.select_button')}
									</button>
								</>
							)}
						</div>
					</div>
					<br/>
					<div>
						<p className="text-dark" style={{textAlign: "center"}}>{t('upload_cv.accepted_formats')}</p>
					</div>
				</>
			);
		} else {
			return (
				<>
					{getPdfPreview(file)}
					<br/>
					<div style={{
						width: "100%", backgroundColor: "rgba(0,0,0,0.03)", display: "flex",
						alignItems: "center", padding: "0 10px", height: "50px",
						boxSizing: "border-box", borderRadius: "5px", gap: "5px"
					}}>
						<Icon path={mdiFileOutline} size={1}/>
						<p className="m-0">{filename}</p>
						<div className="toolbar-spacer"></div>
						<button className="btn-icon" onClick={() => {
							setFile(null);
							setFilename('');
						}}>
							<Icon path={mdiClose} size={1}/>
						</button>
					</div>
					<br/>
					<div className="toolbar-items" onClick={() => {
						handleSubmit();
					}}>
						<div className="toolbar-spacer"></div>
						<button className="btn-filled">{t('upload_cv.upload_button')}
							<Icon path={mdiChevronRight}
							      size={1}/></button>
					</div>
				</>
			);
		}
	};
	
	useEffect(() => {
		setError(null);
	}, [t]);
	
	return (
		<>
			<div className="dashboard-card-toolbar">
				<Link to="../manage-cvs">
					<button className="btn-icon-dashboard"><Icon path={mdiArrowLeft} size={1.4}/>
					</button>
				</Link>
				<h1>{t('upload_cv.title')}</h1>
			</div>
			<div className="dashboard-card" style={{maxWidth: "900px"}}>
				<section>
					{getStep()}
					{error && <p className="text-red">{error}</p>}
				</section>
			</div>
		</>
	);
};

export default UploadCV;