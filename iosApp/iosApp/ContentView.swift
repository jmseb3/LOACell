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
            }
        }
    }
}
