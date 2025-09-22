# Crypto Trading Dashboard

Um aplicativo Android moderno para acompanhar preços de criptomoedas em tempo real, desenvolvido com Kotlin e Jetpack Compose.

## 🏗️ Arquitetura

O aplicativo segue a arquitetura MVVM (Model-View-ViewModel) com os seguintes componentes:

- **Data Layer**: Models, API services e Repository
- **UI Layer**: Telas Compose, componentes reutilizáveis e navegação
- **ViewModel Layer**: Gerenciamento de estado e lógica de negócio

## 🚀 Funcionalidades

### ✅ Core Features Implementadas

1. **API Integration**
   - Integração com CoinGecko API para dados de mercado
   - Busca das 20 principais criptomoedas por volume

2. **Dashboard Home Screen**
   - Lista das 20 principais criptomoedas
   - Exibição de preço atual, variação 24h e volume
   - Sparklines (gráficos miniatura) para cada moeda
   - Abas para alternar entre:
     - Top 20 por Volume
     - Maiores Ganhos
     - Maiores Perdas
   - Pull-to-refresh para atualizar dados

3. **Currency Detail Screen**
   - Informações detalhadas da criptomoeda
   - Preço em tempo real, máximo/mínimo de 24h, volume
   - Gráfico histórico de preços (1D, 7D, 30D, 1Y)
   - Lista de pares de negociação comuns
   - Estatísticas do gráfico (preço atual, variação, máximo/mínimo)

### 🛠️ Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **UI Framework**: Jetpack Compose com Material 3
- **Arquitetura**: MVVM
- **Rede**: Retrofit + OkHttp
- **Concorrência**: Coroutines + Flow
- **Injeção de Dependência**: Manual (ViewModels criados com viewModel())
- **Imagens**: Coil para carregamento de ícones

### 📱 Componentes de UI

- **CryptoCard**: Card para exibir informações de cada criptomoeda
- **SparklineChart**: Gráfico miniatura usando Canvas do Compose
- **PriceChart**: Gráfico detalhado de preços históricos
- **PriceChartStats**: Estatísticas do gráfico de preços

## 🏃‍♂️ Como Executar

### Pré-requisitos

1. **Android Studio** (versão Arctic Fox ou superior)
2. **JDK 11** ou superior
3. **Android SDK** com API 24+ (Android 7.0)

### Passos para execução

1. **Clone o projeto**:
   ```bash
   git clone <repository-url>
   cd crypto-trading-dashboard
   ```

2. **Abra no Android Studio**:
   - Abra o Android Studio
   - Selecione "Open an existing Android Studio project"
   - Navegue até a pasta do projeto e selecione

3. **Sincronize o projeto**:
   - O Android Studio deve sincronizar automaticamente as dependências do Gradle
   - Se necessário, clique em "Sync Project with Gradle Files"

4. **Execute o aplicativo**:
   - Conecte um dispositivo Android ou use um emulador
   - Clique no botão "Run" (ícone de play verde)
   - Selecione o dispositivo alvo

## 🔧 Configuração da API

O aplicativo utiliza a API pública do CoinGecko (https://www.coingecko.com/en/api) que não requer chave de API para uso básico. As chamadas incluem:

- `/coins/markets`: Lista de criptomoedas com dados de mercado
- `/coins/{id}`: Dados detalhados de uma criptomoeda
- `/coins/{id}/market_chart`: Histórico de preços
- `/coins/{id}/tickers`: Pares de negociação

## 📂 Estrutura do Projeto

```
app/src/main/java/com/cryptotrading/dashboard/
├── data/
│   ├── api/           # Interfaces e clientes da API
│   ├── model/         # Modelos de dados
│   └── repository/    # Repositório para gerenciar dados
├── ui/
│   ├── components/    # Componentes reutilizáveis
│   ├── screens/       # Telas principais
│   ├── navigation/    # Configuração de navegação
│   └── theme/         # Tema e cores do app
├── viewmodel/         # ViewModels
└── MainActivity.kt    # Activity principal
```

## 🎨 Tema e Design

- **Tema**: Material 3 com suporte a modo escuro
- **Cores**: Esquema de cores personalizado com tons de verde, azul e laranja
- **Tipografia**: Tipografia padrão do Material 3
- **Componentes**: Cards, listas e gráficos responsivos

## 🔮 Funcionalidades Futuras (Stretch Goals)

As seguintes funcionalidades podem ser implementadas como melhorias:

1. **WebSocket para atualizações ao vivo**
2. **Gráficos de candlestick**
3. **Modo de portfólio para simular investimentos**
4. **Backend Spring Boot com microserviço**
5. **Cache offline com Room**
6. **Notificações de preço**
7. **Comparação de moedas**

## 📋 Notas de Desenvolvimento

- O aplicativo segue as melhores práticas do Android moderno
- Código comentado e bem estruturado
- Tratamento adequado de erros e estados de carregamento
- UI responsiva e acessível
- Suporte a pull-to-refresh em ambas as telas

## 🤝 Contribuição

Para contribuir com o projeto:

1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
