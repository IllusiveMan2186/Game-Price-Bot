import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AppContent from './AppContent';
import Footer from './Footer';

function App() {
  return (
    <div className="App">
      <div className="row">
        <div className="col">
          <AppContent />
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default App;

