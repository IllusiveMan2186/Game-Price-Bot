import './styles/app.css'

import Router from '@routes/Router';
import Footer from '@components/common/layout/footer/Footer';


function App() {
  return (
    <div className="App">
      <div className="row">
        <div className="col">
          <Router />
        </div>
      </div> 
      <Footer />
    </div>
  );
}

export default App;