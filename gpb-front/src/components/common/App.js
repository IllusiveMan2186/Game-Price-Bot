import '../../styles/app.css'
import Router from './Router';
import Footer from './Footer';

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