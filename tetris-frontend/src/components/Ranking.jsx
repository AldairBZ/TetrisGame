import React, { useEffect, useState } from "react";
import { getRanking } from "../api/tetrisApi";

const Ranking = () => {
  const [ranking, setRanking] = useState([]);

  useEffect(() => {
    getRanking().then(data => setRanking(data));
  }, []);

  return (
    <div>
      <h3 style={{ color: "#00ff00" }}>Top 10 Ranking</h3>
      <ol>
        {ranking.map((player, idx) => (
          <li key={idx}>{player.name} - {player.score}</li>
        ))}
      </ol>
    </div>
  );
};

export default Ranking;