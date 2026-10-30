import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split, cross_val_predict, StratifiedKFold
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (classification_report, confusion_matrix,
                             average_precision_score, precision_recall_curve,
                             ConfusionMatrixDisplay)

# 1. Chargement
df = pd.read_csv("bank_transactions.csv")
print("Dimensions :", df.shape)

# 2. Proportion de fraudes et valeurs manquantes
print("\nProportion de fraudes :")
print(df["is_fraud"].value_counts(normalize=True))
print("\nValeurs manquantes :")
print(df.isna().sum())

# 4. Exclusion des identifiants
X = df.drop(columns=["is_fraud", "transaction_id", "account_id"])
y = df["is_fraud"]

# 3. Séparation entraînement/test stratifiée
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, stratify=y, random_state=42)

# 5. Pipeline : imputation, encodage, normalisation
num = ["amount", "hour", "transactions_last_24h", "average_amount_last_7d"]
cat = ["country", "merchant_category"]
pre = ColumnTransformer([
    ("num", Pipeline([("imp", SimpleImputer(strategy="median")),
                      ("sc", StandardScaler())]), num),
    ("cat", Pipeline([("imp", SimpleImputer(strategy="most_frequent")),
                      ("oh", OneHotEncoder(handle_unknown="ignore"))]), cat),
])

# 6. Modèle : régression logistique (class_weight pour le déséquilibre)
model = Pipeline([("pre", pre),
                  ("clf", LogisticRegression(class_weight="balanced", max_iter=1000))])

# Seuil choisi par validation croisée sur le TRAIN uniquement
cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
proba_cv = cross_val_predict(model, X_train, y_train, cv=cv, method="predict_proba")[:, 1]
prec, rec, thr = precision_recall_curve(y_train, proba_cv)
f1 = 2 * prec[:-1] * rec[:-1] / (prec[:-1] + rec[:-1] + 1e-9)
threshold = thr[np.argmax(f1)]
print(f"\nSeuil retenu (CV sur le train) : {threshold:.3f}")

model.fit(X_train, y_train)

# 7. Évaluation sur le test (une seule fois)
proba = model.predict_proba(X_test)[:, 1]
y_pred = (proba >= threshold).astype(int)

print("\nRapport de classification :")
print(classification_report(y_test, y_pred, digits=3))
cm = confusion_matrix(y_test, y_pred)
print("Matrice de confusion :\n", cm)
print("Average precision :", round(average_precision_score(y_test, proba), 4))

# Figures pour le rapport
ConfusionMatrixDisplay(cm, display_labels=["Légitime", "Fraude"]).plot()
plt.title("Matrice de confusion (test)")
plt.savefig("confusion_matrix.png", dpi=120, bbox_inches="tight")
plt.close()

p, r, _ = precision_recall_curve(y_test, proba)
plt.plot(r, p)
plt.xlabel("Rappel"); plt.ylabel("Précision")
plt.title("Courbe précision-rappel (test)")
plt.savefig("precision_recall.png", dpi=120, bbox_inches="tight")