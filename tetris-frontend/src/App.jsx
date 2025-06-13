import React, { useState } from "react";
import SplashScreen from "./components/SplashScreen";

function App() {
  const [showSplash, setShowSplash] = useState(true);

  return (
    <div>
      {showSplash && <SplashScreen onFinish={() => setShowSplash(false)} />}
      {!showSplash && (
        <div>
          {/* Aquí irá tu TetrisBoard y demás componentes */}
          <h2 style={{ color: "#00ff00", textAlign: "center", marginTop: "2rem" }}>
            ¡Aquí irá el juego!
          </h2>
        </div>
      )}
    </div>
  );
}

export default App;