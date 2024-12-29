import React, { act } from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { BrowserRouter as Router } from 'react-router-dom';
import { AuthContext } from '../../../providers/AuthProvider';
import CreerOffreStage from '../CreerOffreStage';

// Mocking react-i18next
jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
        i18n: { changeLanguage: jest.fn() }
    }),
    I18nextProvider: ({ children }) => <>{children}</>
}));

const mockAuthContext = {
    currentToken: 'mockToken',
    currentUser: { role: 'ADMIN' }
};

// Clear fetch mock after each test
afterEach(() => {
    jest.resetAllMocks();
});

describe('CreerOffreStage Component', () => {
    test('renders without crashing', () => {
        render(
            <AuthContext.Provider value={mockAuthContext}>
                <Router>
                    <CreerOffreStage />
                </Router>
            </AuthContext.Provider>
        );
        expect(screen.getByText(/creer_offre_stage_page.employeurs/i)).toBeInTheDocument();
    });

    test('renders employeurs select options for ADMIN', async () => {
        const mockEmployeurs = [
            { id: 1, fullName: 'Employer One' },
            { id: 2, fullName: 'Employer Two' }
        ];

        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: true,
                json: () => Promise.resolve(mockEmployeurs)
            })
        );

        await act(async () => {
            render(
                <AuthContext.Provider value={mockAuthContext}>
                    <Router>
                        <CreerOffreStage />
                    </Router>
                </AuthContext.Provider>
            );
        });

        expect(screen.getByText(/creer_offre_stage_page.employeurs/i)).toBeInTheDocument();
        expect(screen.getByRole('option', { name: 'Employer One' })).toBeInTheDocument();
        expect(screen.getByRole('option', { name: 'Employer Two' })).toBeInTheDocument();
    });

    test('Does not render employeurs select options for EMPLOYEUR', async () => {
        const mockAuthContextEmployeur = {
            currentToken: 'mockToken',
            currentUser: { role: 'EMPLOYEUR' }
        };

        await act(async () => {
            render(
                <AuthContext.Provider value={mockAuthContextEmployeur}>
                    <Router>
                        <CreerOffreStage />
                    </Router>
                </AuthContext.Provider>
            );
        });

        global.fetch = jest.fn(() =>
            Promise.resolve({
                ok: true,
                json: () => Promise.resolve([]), // Mock empty response for EMPLOYEUR role
            })
        );

        // Assert that the employeurs select is not in the document
        expect(screen.queryByText(/creer_offre_stage_page.employeurs/i)).not.toBeInTheDocument();
    });

});
