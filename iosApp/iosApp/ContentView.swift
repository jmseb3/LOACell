import SwiftUI
import SwiftUI_Snackbar
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()

    let bottomHeight : CGFloat = 80
    var body: some View {
        ZStack {
            VStack {
                if(viewModel.user == nil) {
                    LoginView()
                } else {
                    VStack {
                        DialogHost(
                            dialogStatus: viewModel.dialogStatus,
                            dialogAction: viewModel.getDialogAction()
                        ) {
                            SnackBarHost(bottomSpace: bottomHeight + 20) {
                                TopAppBar()
                                Divider()
                                MainContent()
                                BottomAppBar(height: bottomHeight)
                            }
                        }
                    }
                }
            }
            if viewModel.showSplash {
                VStack(alignment : .center) {
                    Image(resource: \.logo)
                }
                .frame(maxWidth: .infinity,maxHeight: .infinity)
                .background(.white)
            }
        }
        .environmentObject(viewModel)
        .environmentObject(viewModel.sc)
    }
}
