// Footer.test.jsx
import React from 'react';
import { render, screen } from '@testing-library/react';
import Footer from './Footer';
import config from "@root/config";

// Мокаем Message и другие компоненты
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>,
}));

jest.mock('@components/common/localization/Localization', () => () => <div>Localization</div>);
jest.mock('@components/common/messenger/Messenger', () => () => <div>Messenger</div>);

describe('Footer', () => {
  it('renders footer content correctly', () => {
    render(<Footer />);

    expect(screen.getByText('Game Price Bot')).toBeInTheDocument();
    expect(screen.getByText('app.footer.gpb.info')).toBeInTheDocument();
    expect(screen.getByText('app.footer.contact')).toBeInTheDocument();
    expect(screen.getByText(config.SUPPORT_EMAIL)).toBeInTheDocument();
    expect(screen.getByText('Localization')).toBeInTheDocument();
    expect(screen.getByText('Messenger')).toBeInTheDocument();
  });
});
