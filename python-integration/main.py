import requests
import json

BASE_URL = "http://localhost:8080/api"

def verificar_status():
    try:
        response = requests.get(f"{BASE_URL}/status")
        if response.status_code == 200:
            print("Servidor Java está Online!")
            print(response.json())
        else:
            print(f"Erro ao conectar: {response.status_code}")
    except Exception as e:
        print(f"Erro: {e}")

def listar_integracoes():
    try:
        response = requests.get(f"{BASE_URL}/integracoes")
        if response.status_code == 200:
            integracoes = response.json()
            print("\nLista de Integrações:")
            for i in integracoes:
                print(f"ID: {i['id']} | Nome: {i['nome']} | Linguagem: {i['linguagem']} | Status: {i['status']}")
        else:
            print(f"Erro ao listar: {response.status_code}")
    except Exception as e:
        print(f"Erro: {e}")

def criar_integracao(nome, linguagem, status):
    payload = {
        "nome": nome,
        "linguagem": linguagem,
        "status": status
    }
    try:
        response = requests.post(f"{BASE_URL}/integracoes", json=payload)
        if response.status_code == 200:
            print(f"\nIntegração {nome} criada com sucesso!")
            print(response.json())
        else:
            print(f"Erro ao criar: {response.status_code}")
    except Exception as e:
        print(f"Erro: {e}")

if __name__ == "__main__":
    # Exemplo de uso
    print("--- Teste de Integração Python -> Java (Protheus) ---")
    # criar_integracao("Protheus_POUI", "Python", "Ativo")
    # listar_integracoes()
