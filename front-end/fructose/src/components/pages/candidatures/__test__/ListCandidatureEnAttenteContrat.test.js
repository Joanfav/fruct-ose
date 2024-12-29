import { React, act } from 'react';
import { render, screen, waitFor, fireEvent} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { BrowserRouter as Router } from 'react-router-dom';
import { AuthContext } from '../../../providers/AuthProvider';
import { CandidatureContext } from '../../../providers/CandidatureProvider';
import ListCandidatureEnAttenteContrat from '../ListCandidatureEnAttenteContrat';

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
        i18n: { changeLanguage: jest.fn() }
    }),
    I18nextProvider: ({ children }) => <>{children}</>
}));

const mockAuthContext = {
    currentToken: 'validToken'
};

const mockCandidature = [
    {
        id: 1,
        etudiantDTO: { fullName: 'John Doe' },
        offreStageDTO: { ownerDTO: { fullName: 'Company A' }, nom: 'Stage A' }
    },
    {
        id: 2,
        etudiantDTO: { fullName: 'Jane Smith' },
        offreStageDTO: { ownerDTO: { fullName: 'Company B' }, nom: 'Stage B' }
    }
];

global.fetch = jest.fn(() =>
    Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockCandidature)
    })
);

beforeEach(() => {
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: true,
            json: () => Promise.resolve(mockCandidature)
        })
    );
});

describe('ListCandidatureEnAttenteContrat Component', () => {
    test('renders candidature mock successfully', async () => {
        await act(async () => {
            render(
                <AuthContext.Provider value={mockAuthContext}>
                    <CandidatureContext.Provider value={{ fetchCandidatureByEtatAccepteApresEntrevue: () => Promise.resolve(mockCandidature) }}>
                        <Router>
                            <ListCandidatureEnAttenteContrat />
                        </Router>
                    </CandidatureContext.Provider>
                </AuthContext.Provider>
            );
        });

        expect(screen.getByText('John Doe - Company A - Stage A')).toBeInTheDocument();
        expect(screen.getByText('Jane Smith - Company B - Stage B')).toBeInTheDocument();
    });

    test('opens modal on click', async () => {
        await act(async () => {
            render(
                <AuthContext.Provider value={mockAuthContext}>
                    <CandidatureContext.Provider value={{ fetchCandidatureByEtatAccepteApresEntrevue: () => Promise.resolve(mockCandidature) }}>
                        <Router>
                            <ListCandidatureEnAttenteContrat />
                        </Router>
                    </CandidatureContext.Provider>
                </AuthContext.Provider>
            );
        });

        fireEvent.click(screen.getByText('John Doe - Company A - Stage A'));
        expect(screen.getByText('modal.generate_pdf')).toBeInTheDocument();
    });

    test('closes modal on save', async () => {
        await act(async () => {
            render(
                <AuthContext.Provider value={mockAuthContext}>
                    <CandidatureContext.Provider value={{ fetchCandidatureByEtatAccepteApresEntrevue: () => Promise.resolve(mockCandidature) }}>
                        <Router>
                            <ListCandidatureEnAttenteContrat />
                        </Router>
                    </CandidatureContext.Provider>
                </AuthContext.Provider>
            );
        });

        fireEvent.click(screen.getByText('John Doe - Company A - Stage A'));
        fireEvent.click(screen.getByText('modal.generate'));
        await waitFor(() => {
            expect(screen.queryByText('modal.generate_pdf')).not.toBeInTheDocument();
        });
    });

    test('closes modal on close', async () => {
        await act(async () => {
            render(
                <AuthContext.Provider value={mockAuthContext}>
                    <CandidatureContext.Provider value={{ fetchCandidatureByEtatAccepteApresEntrevue: () => Promise.resolve(mockCandidature) }}>
                        <Router>
                            <ListCandidatureEnAttenteContrat />
                        </Router>
                    </CandidatureContext.Provider>
                </AuthContext.Provider>
            );
        });

        fireEvent.click(screen.getByText('John Doe - Company A - Stage A'));
        fireEvent.click(screen.getByText('modal.close'));
        await waitFor(() => {
            expect(screen.queryByText('modal.generate_pdf')).not.toBeInTheDocument();
        });
    });
});