import SwiftUI
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()
    
    
    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView(viewModel: viewModel)
            } else {
                MainContent(viewModel: viewModel)
                Button("out") {
                    LoginHelper.instance.signOut()
                }
                if(viewModel.syncData) {
                    ProgressView(label: {
                        Text("데이터를 동기화 중입니다.")
                    })
                } else {
                    Text("123")
                }
            }
        }
    }
}
