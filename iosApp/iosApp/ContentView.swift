import SwiftUI
import SwiftUI_Snackbar
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()

    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView()
            } else {
                SnackBarHost() {
                    TopAppBar()
                    Divider()
                    MainContent()
                    Spacer().frame(height: 10)
                }
                BottomAppBar()
            }
        }
        .environmentObject(viewModel)
        .environmentObject(viewModel.sc)
    }
}
