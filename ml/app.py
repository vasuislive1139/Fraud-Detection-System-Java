from flask import Flask, request, jsonify
import random

app = Flask(__name__)

@app.route("/predict", methods=["POST"])
def predict_fraud():
    tx = request.json
    
    score = 0
    anomalies = []
    
    amount = float(tx.get("amount", 0))
    tx_type = tx.get("type", "")
    oldbalanceOrg = float(tx.get("oldbalanceOrg", 0))
    newbalanceDest = float(tx.get("newbalanceDest", 0))
    oldbalanceDest = float(tx.get("oldbalanceDest", 0))
    
    if amount > 1000000:
        score += 40
        anomalies.append("EXTREME_AMOUNT_OUTLIER")
        
    if tx_type == "TRANSFER" and oldbalanceOrg == amount:
        score += 50
        anomalies.append("FULL_ACCOUNT_DEPLETION")
        
    if tx_type == "CASH_OUT" and newbalanceDest == 0 and oldbalanceDest == 0:
        score += 30
        anomalies.append("ZERO_DESTINATION_HISTORY")

    noise = random.randint(0, 10)
    final_score = min(100, score + noise)
    
    confidence = 0.50 + (len(anomalies) * 0.15)
    confidence = min(0.99, confidence)

    return jsonify({
        "ml_score": final_score,
        "confidence": confidence,
        "anomalies_detected": anomalies
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
